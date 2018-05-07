package ua.com.papers.services.crawler.unit.uran;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import ua.com.papers.crawler.core.main.bo.Page;
import ua.com.papers.crawler.settings.v1.Part;
import ua.com.papers.crawler.settings.v1.PageHandlerV1;
import ua.com.papers.crawler.settings.v1.PostHandle;
import ua.com.papers.crawler.settings.v1.PreHandle;
import ua.com.papers.crawler.core.processor.convert.StringAdapter;
import ua.com.papers.services.crawler.BasePublicationHandler;
import ua.com.papers.services.crawler.IHandlerCallback;
import ua.com.papers.crawler.core.processor.convert.UrlAdapter;
import ua.com.papers.crawler.util.*;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.services.authors.IAuthorService;

import javax.validation.constraints.NotNull;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * <p>
 * An example of publication handler. This class
 * handles pages like http://journals.uran.ua/index.php/1991-0177/issue/view/4013
 * </p>
 * Created by Максим on 2/8/2017.
 */
@Log
@Value
@Getter(AccessLevel.NONE)
@PageHandlerV1(id = 2)
public class UranPublicationHandler extends BasePublicationHandler {

    private static final int GROUP_ID = 1;

    IHandlerCallback callback;
    @NonFinal
    PublicationView publicationView;
    @NonFinal
    SoftReference<Map<String, Integer>> fullNameToId;

    public UranPublicationHandler(IAuthorService authorService, IHandlerCallback callback, List<AuthorEntity> authorEntities) {
        super(authorService);
        this.callback = Preconditions.checkNotNull(callback);

        val cache = new HashMap<String, Integer>();
        fullNameToId = new SoftReference<>(cache);

        for (val entity : authorEntities) {

            String key = entity.getLastName() + (TextUtils.isEmpty(entity.getInitials()) ? "" : entity.getInitials())
                    .trim();

            cache.put(key, entity.getId());
        }
    }

    @PreHandle
    public void onPrepare(Page page) throws WrongRestrictionException {
        //log.log(Level.INFO, String.format("#onPrepare %s, url %s", getClass(), page.getUrl()));

        if (fullNameToId == null || fullNameToId.get() == null) {
            //log.log(Level.INFO, "saving authors into cache");

            val cache = new HashMap<String, Integer>();
            fullNameToId = new SoftReference<>(cache);

            /*try {

                List<AuthorEntity> res = authorService.getAuthors(0, -1, null);

                for (val entity : res) {

                    String key = entity.getLastName() + (TextUtils.isEmpty(entity.getInitials()) ? "" : entity.getInitials())
                            .trim();

                    cache.put(key, entity.getId());
                }

                *//*fullNameToId = authorService.getAuthors(0, -1, null)
                        .stream()
                        .collect(Collectors.toMap(
                                a -> (a.getLastName() + (TextUtils.isEmpty(a.getInitials()) ? "" : a.getInitials()))
                                        .trim(),// probably overdo but it guaranties valid key even
                                // if it was incorrectly saved
                                AuthorEntity::getId));*//*
            } catch (final NoSuchEntityException e) {//FIXME if db is empty
                log.log(Level.WARNING, "NoSuchEntityException, FIXME", e);
            }*/
        }
    }

    @PostHandle
    public void onPageEnd(Page page) {
        //log.log(Level.INFO, String.format("#onPageEnd %s, url %s", getClass(), page.getUrl()));
    }

    @Part(id = 1, group = GROUP_ID, converter = UrlAdapter.class)
    public void onHandleUrl(URL url) {
        //log.log(Level.INFO, String.format("#onHandleUrl %s, %s", getClass(), url));

        if (url == null) {
            log.log(Level.WARNING, "Failed to parse document url");
        } else {
            publicationView.setLink(url.toExternalForm());
        }
    }

    @Part(id = 2, group = GROUP_ID, converter = StringAdapter.class)
    public void onHandleTitle(String title) {
        //log.log(Level.INFO, String.format("#onHandleTitle %s", getClass()));
        publicationView.setTitle(title);
    }

    @Part(id = 3, group = GROUP_ID, converter = StringAdapter.class)
    public void onHandleAuthors(String authors) {
        //log.log(Level.INFO, String.format("#onHandleAuthors %s, %s", getClass(), authors));
        publicationView.setAuthors_id(getAuthorIdsByNames(authors.trim().replaceAll("\\s*,\\s*", ",").split(",")));
    }

    @PreHandle(group = GROUP_ID)
    public void prePublication() {
        //log.log(Level.INFO, String.format("#prePublication %s", getClass()));
        this.publicationView = new PublicationView();
        this.publicationView.setStatus(PublicationStatusEnum.ACTIVE);
        this.publicationView.setType(PublicationTypeEnum.ARTICLE);
    }

    @PostHandle(group = GROUP_ID)
    public void postPublication() {
        //log.log(Level.INFO, String.format("#postPublication %s", getClass()));

        val isValid = !TextUtils.isEmpty(publicationView.getLink())
                && !TextUtils.isEmpty(publicationView.getTitle())
                && publicationView.getAuthors_id() != null && !publicationView.getAuthors_id().isEmpty();

        if (isValid) {
            callback.onPublicationReady(publicationView);
            //log.log(Level.INFO, String.format("publication were processed successfully, %s", publicationView.getLink()));
        } else {
            log.log(Level.WARNING, "failed to process publication");
        }
    }

    private List<Integer> getAuthorIdsByNames(String[] fullNames) {
        final ArrayList<Integer> result = new ArrayList<>(fullNames.length);

        Integer id = null;

        for (final String fullName : fullNames) {
            //log.log(Level.INFO, String.format("full name %s", fullName));

            Map<String, Integer> cached = fullNameToId.get();

            if (cached == null || (id = cached.get(fullName)) == null) {
                // create new author
                try {
                    String nameTemp = fullName;
                    if (nameTemp.contains("(") && nameTemp.contains(")")) {
                        nameTemp = nameTemp.substring(nameTemp.indexOf("("), nameTemp.indexOf(")") + 1);
                    }
                    if (nameTemp.contains("[") && nameTemp.contains("]")) {
                        nameTemp = nameTemp.substring(nameTemp.indexOf("["), nameTemp.indexOf("]") + 1);
                    }
                    String[] credentialsArr = nameTemp.split("\\s");// first, middle and last names
                    //AuthorView authorView = new AuthorView();
                    if (credentialsArr.length < 2) continue;
                    final String initials, lastName;
                    if (credentialsArr.length == 2) {
                        lastName = credentialsArr[1];
                        initials = String.format("%S.", credentialsArr[0].charAt(0));
                    } else {
                        lastName = credentialsArr[2];
                        initials = String.format("%S. %S.", credentialsArr[0].charAt(0), credentialsArr[1].charAt(0));
                    }

                    val foundId = findAuthorId(initials, lastName, fullName);

                    if (cached == null) {
                        // soft reference was released
                        cached = new HashMap<>();
                        fullNameToId = new SoftReference<>(cached);
                    }

                    cached.put(fullName, id = foundId);
                } catch (final ServiceErrorException | NoSuchEntityException e) {
                    log.log(Level.WARNING, "Service error occurred while saving publication Uran", e);
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Fatal error occurred while saving publication Uran", e);
                }
            }
            if (id != null) {
                result.add(id);
            }
        }
        result.trimToSize();
        return result;
    }

    @NotNull
    private String[] parseFullName(String fullName) {
        val i = fullName.indexOf(' ');
        return i < 0 ? new String[]{fullName} : new String[]{fullName.substring(0, i), fullName.substring(i + 1)};
    }

}
