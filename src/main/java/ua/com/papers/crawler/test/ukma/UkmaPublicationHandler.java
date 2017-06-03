package ua.com.papers.crawler.test.ukma;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.format.convert.StringAdapter;
import ua.com.papers.crawler.test.IHandlerCallback;
import ua.com.papers.crawler.test.UrlAdapter;
import ua.com.papers.crawler.util.*;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.view.AuthorMasterView;
import ua.com.papers.pojo.view.AuthorView;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.services.authors.IAuthorService;

import javax.validation.constraints.NotNull;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

/**
 * <p>
 * An example of publication handler. This class
 * handles pages like
 * </p>
 * Created by Максим on 2/8/2017.
 */
@Log
@Value
@Getter(AccessLevel.NONE)
@PageHandler(id = 5)
public class UkmaPublicationHandler {

    private static final int GROUP_ID = 2;
    static String PAGE_REGEX = "(\\[\\])|([Сс]\\.\\s+\\d+\\.?\\s*-?\\s*\\d+\\.?)";

    IHandlerCallback callback;
    IAuthorService authorService;
    @NonFinal
    PublicationView publicationView;
    @NonFinal
    SoftReference<Map<String, Integer>> fullNameToId;

    public UkmaPublicationHandler(IAuthorService authorService, IHandlerCallback callback, List<AuthorEntity> authorEntities) {
        this.authorService = Preconditions.checkNotNull(authorService);
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
        log.log(Level.INFO, String.format("#onPrepare %s, url %s", getClass(), page.getUrl()));

        if (fullNameToId == null || fullNameToId.get() == null) {
            log.log(Level.INFO, "saving authors into cache");

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
        log.log(Level.INFO, String.format("#onPageEnd %s, url %s", getClass(), page.getUrl()));
    }

    @Handler(id = 7, group = GROUP_ID)
    public void onHandleTitle(Element element) {
        log.log(Level.INFO, String.format("#onHandleTitle %s", getClass()));

        element.select("strong").remove();
        element.getElementsByTag("a").remove();

        val text = element.text().replaceAll(PAGE_REGEX, "");

        publicationView.setTitle(text);
    }

    @Handler(id = 8, converter = StringAdapter.class, group = GROUP_ID)
    public void onHandleAuthors(String authorsStr) {
        log.log(Level.INFO, String.format("#onHandleAuthors %s, %s", getClass(), authorsStr));
        publicationView.setAuthorsId(getAuthorIdsByNames(authorsStr.trim().replaceAll("\\s*,\\s*", ",").split(",")));
    }

    @Handler(id = 9, converter = UrlAdapter.class, group = GROUP_ID)
    public void onHandleUrl(URL url) {
        log.log(Level.INFO, String.format("#onHandleUrl %s, %s", getClass(), url));

        if (url == null) {
            log.log(Level.WARNING, "Failed to parse document url");
        } else {
            publicationView.setLink(url.toExternalForm());
        }
    }

    @PreHandle(group = GROUP_ID)
    public void prePublication() {
        log.log(Level.INFO, String.format("#prePublication %s", getClass()));
        this.publicationView = new PublicationView();
        this.publicationView.setStatus(PublicationStatusEnum.ACTIVE);
        this.publicationView.setType(PublicationTypeEnum.ARTICLE);
    }

    @PostHandle(group = GROUP_ID)
    public void postPublication() {
        log.log(Level.INFO, String.format("#postPublication %s", getClass()));

        val isValid = !TextUtils.isEmpty(publicationView.getLink())
                && !TextUtils.isEmpty(publicationView.getTitle())
                && publicationView.getAuthorsId() != null && !publicationView.getAuthorsId().isEmpty();

        if (isValid) {
            callback.onPublicationReady(publicationView);
            log.log(Level.INFO, String.format("publication were processed successfully, %s", publicationView.getLink()));
        } else {
            log.log(Level.WARNING, "failed to process publication");
        }
    }

    private List<Integer> getAuthorIdsByNames(String[] fullNames) {
        final ArrayList<Integer> result = new ArrayList<>(fullNames.length);

        Integer id = null;

        for (final String fullName : fullNames) {
            log.log(Level.INFO, String.format("full name %s", fullName));

            if (fullNameToId.get() == null
                    || (id = fullNameToId.get().get(fullName)) == null) {
                // create new author
                val credentialsArr = parseFullName(fullName);
                val authorView = new AuthorView();

                log.log(Level.INFO, String.format("parsed %s as %s", fullName, Arrays.toString(credentialsArr)));

                if (credentialsArr.length != 2) continue;

                authorView.setLast_name(credentialsArr[0]);
                authorView.setInitials(credentialsArr[1]);

                try {
                    //FIXME when I should take it??
                    // --------------------------------------
                    AuthorMasterView masterView = new AuthorMasterView();

                    masterView.setLast_name(credentialsArr[0]);
                    masterView.setInitials(credentialsArr[1]);

                    val masterId = authorService.createAuthorMaster(masterView);
                    // --------------------------------------
                    // create author and grab his id
                    authorView.setMaster_id(masterId);
                    authorView.setOriginal("original");// what?
                    id = authorService.createAuthor(authorView);

                    if (fullNameToId.get() != null) {
                        fullNameToId.get().put(fullName, id);
                    }
                } catch (final ServiceErrorException | NoSuchEntityException e) {
                    log.log(Level.WARNING, "Service error occurred while saving publication", e);
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Fatal error occurred while saving publication", e);
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
