package ua.com.papers.crawler;

import com.google.common.base.Preconditions;
import lombok.extern.java.Log;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.convert.StringAdapter;
import ua.com.papers.crawler.util.*;
import ua.com.papers.pojo.entities.PublisherEntity;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.publisher.IPublisherService;

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
public class PublisherHandler {

    private final IHandlerCallback callback;
    private final IPublisherService publisherService;

    private Map<String, Integer> titleToId;

    private PublisherView publisherView;

    public PublisherHandler(IPublisherService publisherService, IHandlerCallback callback) {
        this.publisherService = Preconditions.checkNotNull(publisherService);
        this.callback = Preconditions.checkNotNull(callback);
    }

    @PreHandle
    public void onPrepare() {

        if (titleToId == null) {
            // load all data
            try {
                titleToId = publisherService.getPublishers(0, 0, null)
                        .stream()
                        .collect(Collectors.toMap(PublisherEntity::getTitle, PublisherEntity::getId));
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        publisherView = new PublisherView();
    }

    @PostHandle
    public void onPageParsed(Page page) {

        Preconditions.checkNotNull(publisherView, "inner exception! onPrepare# wasn't called");

        if (TextUtils.isEmpty(publisherView.getTitle())) {
            log.log(Level.WARNING, String.format("No title parsed for page %s", page.getUrl()));
            callback.onHandleFailure();
        } else {

            final Integer id = titleToId.get(publisherView.getTitle());

            if (id == null) {

                try {
                    publisherView.setId(publisherService.createPublisher(publisherView));
                    callback.onPublisherReady(publisherView);
                } catch (final Exception e) {
                    log.log(Level.WARNING, String.format("failed to create publisher, title %s, page %s",
                            publisherView.getTitle(), page.getUrl()), e);
                    callback.onHandleFailure(e);
                }
            } else {
                publisherView.setId(id);
                callback.onPublisherReady(publisherView);
            }
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
