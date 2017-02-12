package ua.com.papers.crawler.core.domain.format;

import com.google.common.base.Preconditions;
import lombok.Value;
import lombok.val;
import org.jsoup.select.Elements;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.settings.FormatTemplate;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Максим on 12/17/2016.
 */
@Value
public class PageFormatter implements IPageFormatter {

    Collection<? extends FormatTemplate> formatTemplates;
    MapperFunct mapper;

    private static class MapperFunct implements Function<FormatTemplate, Elements> {

        Page page;
        int allocSize;

        public MapperFunct() {
        }

        @Override
        public Elements apply(FormatTemplate t) {
            val elems = page.toDocument().select(t.getCssSelector());

            if (allocSize < elems.size()) {
                allocSize = elems.size();
            }
            return elems;
        }

    }

    public PageFormatter(@NotNull Collection<? extends FormatTemplate> formatTemplates) {
        this.formatTemplates = Collections.unmodifiableCollection(Preconditions.checkNotNull(formatTemplates));
        this.mapper = new MapperFunct();
    }

    @Override
    public List<RawContent> format(@NotNull PageID id, @NotNull Page page) {

        mapper.page = page;
        mapper.allocSize = 0;

        val idToParts = formatTemplates
                .stream()
                .collect(Collectors.toMap(FormatTemplate::getId, mapper));
        val result = new ArrayList<RawContent>(mapper.allocSize);

        for(int i = 0; i < mapper.allocSize; ++i) {

            RawContent content;

            for (val partID : idToParts.keySet()) {

                if (i == result.size() - 1) {
                    content = result.get(i);
                } else {
                    content = new RawContent(id.getId());
                    result.add(content);
                }

                val elems = idToParts.get(partID);

                if (i < elems.size()) {
                    content.putElement(partID, elems.get(i));
                }

                result.set(i, content);
            }
        }
        return result;
    }
}
