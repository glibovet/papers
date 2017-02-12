package ua.com.papers.crawler.test;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.convert.StringAdapter;
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
@PageHandler(id = 3)
@Value
@Getter(AccessLevel.NONE)
public class PublisherHandler {

    IHandlerCallback callback;
    IPublisherService publisherService;
    PublisherView publisherView;

    @NonFinal
    private Map<String, Integer> titleToId;

    public PublisherHandler(IPublisherService publisherService, IHandlerCallback callback) {
        this.publisherService = Preconditions.checkNotNull(publisherService);
        this.callback = Preconditions.checkNotNull(callback);
        this.publisherView = new PublisherView();
    }

    @PreHandle
    public void onPrepare() throws WrongRestrictionException, NoSuchEntityException {

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

        Preconditions.checkNotNull(publisherView, "inner exception! onPrepare# wasn't called");

        if (TextUtils.isEmpty(publisherView.getTitle())) {
            log.log(Level.WARNING, String.format("No title parsed for page %s", page.getUrl()));
            callback.onHandleFailure();
        } else {

            Integer id = titleToId.get(publisherView.getTitle());

            if (id == null) {

                try {
                    id = publisherService.createPublisher(publisherView);
                    titleToId.put(publisherView.getTitle(), id);
                } catch (final Exception e) {
                    log.log(Level.WARNING, String.format("failed to create publisher, title %s, page %s",
                            publisherView.getTitle(), page.getUrl()), e);
                    callback.onHandleFailure(e);
                    return;
                }
            }

            publisherView.setId(id);
            callback.onPublisherReady(publisherView);
        }
    }

    @Handler(id = 1, converter = StringAdapter.class)
    public void onHandlePubHouseTitle(String title) {

        if (!TextUtils.isEmpty(title)) {
            publisherView.setTitle(title.replaceAll("[\\[\\],]", ""));
        }
    }

    @Handler(id = 2, converter = StringAdapter.class)
    public void onHandlePubHouseCity(String city) {
        //todo finish
    }

}
