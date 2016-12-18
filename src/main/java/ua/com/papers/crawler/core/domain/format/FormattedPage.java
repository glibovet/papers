package ua.com.papers.crawler.core.domain.format;

import com.google.common.base.Preconditions;
import lombok.Value;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.core.domain.vo.PartID;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

/**
 * <p>
 * Represents formatted page which were created for example by applying
 * format filters
 * </p>
 * Created by Максим on 12/17/2016.
 */
@Value
public class FormattedPage {

    PageID pageID;
    Map<PartID, String> idToPart;

    public FormattedPage(@NotNull PageID pageID, @NotNull Map<PartID, String> idToPart) {
        this.pageID = Preconditions.checkNotNull(pageID);
        this.idToPart = Collections.unmodifiableMap(Preconditions.checkNotNull(idToPart));
    }
}
