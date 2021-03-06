package ua.com.papers.services.crawler;

import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;

import javax.validation.constraints.NotNull;

/**
 * Created by Максим on 2/6/2017.
 */
public interface IHandlerCallback {

    void onHandleFailure();

    void onHandleFailure(@NotNull Throwable th);

    void onPublisherReady(@NotNull PublisherView publisherView);

    void onPublicationReady(@NotNull PublicationView publicationView);

}
