package ua.com.papers.crawler.core.domain.format;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import org.jsoup.nodes.Element;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 * Represents formatted page which were created for example by applying
 * format filters
 * </p>
 * Created by Максим on 12/17/2016.
 */
@Value
public class RawContent {

    int pageID;
    @Getter(value = AccessLevel.NONE)
    Map<Integer, Element> idToPart;

    public RawContent(int pageID) {
        this.pageID = pageID;
        this.idToPart = new TreeMap<>();
    }

    void putElement(@NotNull Integer partID, @NotNull Element element) {
        idToPart.put(Preconditions.checkNotNull(partID),
                Preconditions.checkNotNull(element));
    }

    public Map<Integer, Element> getIdToPart() {
        return Collections.unmodifiableMap(idToPart);
    }
}