package ua.com.papers.crawler.test;

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
import ua.com.papers.crawler.util.*;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.view.AuthorMasterView;
import ua.com.papers.pojo.view.AuthorView;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.services.authors.IAuthorService;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

/**
 * <p>
 * An example of publication handler. This class
 * handles page with id 3 specified in 'crawler-settings.xml'
 * </p>
 * Created by Максим on 2/8/2017.
 */
@Log
@Value
@Getter(AccessLevel.NONE)
@PageHandler(id = 3)
public class PublicationHandler {

    static String PAGE_REGEX = "(\\[\\])|([Сс]\\.\\s+\\d+\\.?\\s*-?\\s*\\d+\\.?)";

    IHandlerCallback callback;
    IAuthorService authorService;
    @NonFinal
    PublicationView publicationView;

    Map<String, Integer> fullNameToId;

    public PublicationHandler(IAuthorService authorService, IHandlerCallback callback) {
        this.authorService = Preconditions.checkNotNull(authorService);
        this.callback = Preconditions.checkNotNull(callback);
        this.fullNameToId = new HashMap<>();
    }

    @PreHandle
    public void onPrepare() throws WrongRestrictionException {


        if(fullNameToId.isEmpty()) {

            try {

                List<AuthorEntity> res = authorService.getAuthors(0, -1, null);

                for (val entity : res) {

                    String key = entity.getLastName() + (TextUtils.isEmpty(entity.getInitials()) ? "" : entity.getInitials())
                            .trim();

                    fullNameToId.put(key, entity.getId());
                }

                /*fullNameToId = authorService.getAuthors(0, -1, null)
                        .stream()
                        .collect(Collectors.toMap(
                                a -> (a.getLastName() + (TextUtils.isEmpty(a.getInitials()) ? "" : a.getInitials()))
                                        .trim(),// probably overdo but it guaranties valid key even
                                // if it was incorrectly saved
                                AuthorEntity::getId));*/
            } catch (final NoSuchEntityException e) {//FIXME if db is empty
                log.log(Level.WARNING, "FIXME", e);
            }
        }
        // reset variable
        publicationView.setAuthorsId(null);
        publicationView.setTitle(null);
        publicationView.setPublisherId(null);
        publicationView.setLink(null);
    }

    @PostHandle
    public void onPageEnd(Page page) {

        /*if (TextUtils.isEmpty(publicationView.getLink())) {
            log.log(Level.WARNING,
                    String.format("Cannot create publication without url, page %s", page.getUrl()));
            callback.onHandleFailure();
            return;
        }

        if (publicationView.getAuthorsId() == null) {
            log.log(Level.WARNING, "Failed to parse author ids, skipping");
            callback.onHandleFailure();
            return;
        }*/

        //callback.onPublicationReady(publicationView);
    }

    @Handler(id = 3, group = 1)
    public void onHandleTitle(Element element) {

        element.select("strong").remove();
        element.getElementsByTag("a").remove();

        val text = element.text().replaceAll(PAGE_REGEX, "");

        publicationView.setTitle(text);
    }

    @Handler(id = 4, converter = StringAdapter.class, group = 1)
    public void onHandleAuthors(String authorsStr) {
        try {
            publicationView.setAuthorsId(getAuthorIdsByNames(authorsStr.trim().replaceAll("\\s*,\\s*", ",").split(",")));
        } catch (Exception e) {
            log.log(Level.WARNING, String.format("Failed to parse author ids for input: %s", authorsStr), e);
        }
    }

    @Handler(id = 5, converter = UrlAdapter.class, group = 1)
    public void onHandleUrl(URL url) {

        if (url == null) {
            log.log(Level.WARNING, "Failed to parse document url");
        } else {
            publicationView.setLink(url.toExternalForm());
        }
    }

    @PreHandle(group = 1)
    public void prePublication() {
        this.publicationView = new PublicationView();
        this.publicationView.setStatus(PublicationStatusEnum.ACTIVE);
        this.publicationView.setType(PublicationTypeEnum.ARTICLE);
    }

    @PostHandle(group = 1)
    public void postPublication() {
        if (!TextUtils.isEmpty(publicationView.getLink()) && publicationView.getAuthorsId() != null) {
            callback.onPublicationReady(publicationView);
        } else {
            callback.onHandleFailure();
        }
    }

    private List<Integer> getAuthorIdsByNames(String[] fullNames) {
        final ArrayList<Integer> result = new ArrayList<>(fullNames.length);

        Integer id;

        for (final String fullName : fullNames) {
            Preconditions.checkArgument(!TextUtils.isEmpty(fullName), "empty full name!");
            log.log(Level.INFO, String.format("full name %s", fullName));

            if ((id = fullNameToId.get(fullName)) == null) {
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
                    fullNameToId.put(fullName, id);
                } catch (final ServiceErrorException | NoSuchEntityException e) {
                    log.log(Level.WARNING, "Service error occurred while saving publication", e);
                } catch (final ValidationException e) {
                    log.log(Level.SEVERE, "Fatal error occurred while saving publication", e);
                    // finish execution immediately and fix error
                    throw new RuntimeException(e);
                }
            }

            result.add(id);
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
