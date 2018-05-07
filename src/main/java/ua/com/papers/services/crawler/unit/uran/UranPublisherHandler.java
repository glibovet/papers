package ua.com.papers.services.crawler.unit.uran;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.settings.v1.Part;
import ua.com.papers.crawler.settings.v1.PageHandlerV1;
import ua.com.papers.crawler.settings.v1.PostHandle;
import ua.com.papers.crawler.settings.v1.PreHandle;
import ua.com.papers.crawler.core.processor.convert.StringAdapter;
import ua.com.papers.services.crawler.IHandlerCallback;
import ua.com.papers.crawler.util.*;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.PublisherEntity;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.publisher.IPublisherService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * <p>
 * An example of page handler. This class
 * handles page with id 3 specified in 'crawler-settings.xml'
 * </p>
 * Created by Максим on 2/6/2017.
 */
@Log
@PageHandlerV1(id = 2)
@Value
@Getter(AccessLevel.NONE)
public class UranPublisherHandler {

    IHandlerCallback callback;
    IPublisherService publisherService;
    PublisherView publisherView;

    @NonFinal
    private Map<String, Integer> titleToId;

    public UranPublisherHandler(IPublisherService publisherService, IHandlerCallback callback) {
        this.publisherService = Preconditions.checkNotNull(publisherService);
        this.callback = Preconditions.checkNotNull(callback);
        this.publisherView = new PublisherView();
    }

    @PreHandle
    public void onPrepare(Page page) throws WrongRestrictionException, NoSuchEntityException {
        //log.log(Level.INFO, String.format("#onPrepare %s, %s", getClass(), page.getUrl()));

        if (titleToId == null) {
            // load all data
            try {
                titleToId = publisherService.getPublishers(0, -1, null)
                        .stream()
                        .collect(Collectors.toMap(PublisherEntity::getTitle, PublisherEntity::getId));
            } catch (final NoSuchEntityException e) {//FIXME if db is empty...OMG, whyyyy
                log.log(Level.WARNING, "FIXME", e);
                titleToId = new HashMap<>();
            }
        }
        // reset instance
        publisherView.setId(null);
        publisherView.setTitle(null);
    }

    @PostHandle
    public void onPageParsed(Page page) {
        //log.log(Level.INFO, String.format("#onPageParsed %s, %s", getClass(), page.getUrl()));

        Preconditions.checkNotNull(publisherView, "inner exception! onPrepare# wasn't called");

        if (TextUtils.isEmpty(publisherView.getTitle())) {
            log.log(Level.WARNING, String.format("No title parsed for page %s", page.getUrl()));
            callback.onHandleFailure();
        } else {

            Integer id = titleToId.get(publisherView.getTitle());

            if (id == null) {

                try {
                    PublisherEntity entity = publisherService.findPublisherByTitle(publisherView.getTitle());
                    if (entity == null)
                        id = publisherService.createPublisher(publisherView);
                    else id = entity.getId();
                    titleToId.put(publisherView.getTitle(), id);
                } catch (final Exception e) {
                    log.log(Level.WARNING, String.format("failed to create publisher Uran, title %s, page %s",
                            publisherView.getTitle(), page.getUrl()), e);
                    callback.onHandleFailure(e);
                    return;
                }
            }

            publisherView.setId(id);
            //log.log(Level.INFO, String.format("publisher was proceeded successfully %s", publisherView.getTitle()));
            callback.onPublisherReady(publisherView);
        }
    }

    @Part(id = 4, converter = StringAdapter.class)
    public void onHandlePubisherTitle(String title) {
        //log.log(Level.INFO, String.format("#onHandlePubHouseTitle %s, %s", getClass(), title));

        if (TextUtils.isEmpty(title)) {
            log.log(Level.WARNING, String.format("failed to parse title, %s", getClass()));
        } else {
            publisherView.setTitle(title);
        }
    }

}
