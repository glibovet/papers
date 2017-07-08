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
import ua.com.papers.pojo.entities.AuthorMasterEntity;
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
        publicationView.setAuthors_id(getAuthorIdsByNames(authorsStr.trim().replaceAll("\\s*,\\s*", ",").split(",")));
    }

    @Handler(id = 9, converter = UrlAdapter.class, group = GROUP_ID)
    public void onHandleUrl(URL url) {
        log.log(Level.INFO, String.format("#onHandleUrl %s, %s", getClass(), url));

        if (url == null) {
            log.log(Level.WARNING, "Failed to parse document url");
        } else {
            publicationView.setFile_link(url.toExternalForm());
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
    public void postPublication(Page page) {
        log.log(Level.INFO, String.format("#postPublication %s", getClass()));

        // save parsed page link
        publicationView.setLink(page.getUrl().toExternalForm());

        val isValid = !TextUtils.isEmpty(publicationView.getLink())
                && !TextUtils.isEmpty(publicationView.getTitle())
                && publicationView.getAuthors_id() != null && !publicationView.getAuthors_id().isEmpty();

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

            if (fullNameToId.get() == null||fullNameToId.get().size()==0
                    || (id = fullNameToId.get().get(fullName)) == null) {
                // create new author
                try {
                    String nameTemp = fullName.replace("(","");
                    nameTemp = fullName.replace(")","");
                    String[] credentialsArr = nameTemp.split("\\s");// first, middle and last names
                    AuthorView authorView = new AuthorView();
                    if (credentialsArr.length < 2) continue;
                    final String initials, lastName;
                    if (credentialsArr.length == 2) {
                        lastName = credentialsArr[0];
                        initials = String.format("%S.", credentialsArr[1].charAt(0));
                    } else {
                        lastName = credentialsArr[0];
                        initials = String.format("%S. %S.", credentialsArr[1].charAt(0), credentialsArr[2].charAt(0));
                    }
                    authorView.setLast_name(lastName);
                    authorView.setInitials(initials);
                    AuthorMasterView masterView = new AuthorMasterView();
                    masterView.setLast_name(lastName);
                    masterView.setInitials(initials);
                    authorView.setOriginal(fullName);
                    AuthorMasterEntity master = authorService.findByNameMaster(lastName,initials);
                    AuthorEntity author = authorService.findByOriginal(authorView.getOriginal());
                    if (master ==null&&author ==null) {
                        id = authorService.createAuthorMaster(masterView);
                        authorView.setMaster_id(id);
                        int authorId = authorService.createAuthor(authorView);
                    }else
                        id =  master.getId();
                    if (fullNameToId.get() != null) {
                        fullNameToId.get().put(fullName, id);
                    }
                } catch (final ServiceErrorException | NoSuchEntityException e) {
                    log.log(Level.WARNING, "Service error occurred while saving publication UKMA", e);
                } catch (final Exception e) {
                    log.log(Level.SEVERE, "Fatal error occurred while saving publication UKMA", e);
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
