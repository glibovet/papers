package ua.com.papers.services.crawler.unit.ukma;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.java.Log;
import lombok.val;
import org.jsoup.nodes.Element;
import ua.com.papers.crawler.core.main.model.Page;
import ua.com.papers.crawler.settings.v1.Part;
import ua.com.papers.crawler.settings.v1.PageHandlerV1;
import ua.com.papers.crawler.settings.v1.PostHandle;
import ua.com.papers.crawler.settings.v1.PreHandle;
import ua.com.papers.crawler.core.processor.convert.general.StringAdapter;
import ua.com.papers.services.crawler.BasePublicationHandler;
import ua.com.papers.services.crawler.IHandlerCallback;
import ua.com.papers.crawler.core.processor.convert.general.UrlAdapter;
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
 * handles pages like
 * </p>
 * Created by Максим on 2/8/2017.
 */
@Log
@Value
@Getter(AccessLevel.NONE)
@PageHandlerV1(id = 5)
public class UkmaPublicationHandler extends BasePublicationHandler {

    private static final int GROUP_ID = 2;
    static String PAGE_REGEX = "(\\[\\])|([Сс]\\.\\s+\\d+\\.?\\s*-?\\s*\\d+\\.?)";

    IHandlerCallback callback;
    @NonFinal
    PublicationView publicationView;
    @NonFinal
    SoftReference<Map<String, Integer>> fullNameToId;

    public UkmaPublicationHandler(IAuthorService authorService, IHandlerCallback callback, List<AuthorEntity> authorEntities) {
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
        }
    }

    @PostHandle
    public void onPageEnd(Page page) {
        //log.log(Level.INFO, String.format("#onPageEnd %s, url %s", getClass(), page.getUrl()));
    }

    @Part(id = 7, group = GROUP_ID)
    public void onHandleTitle(Element element) {
        //log.log(Level.INFO, String.format("#onHandleTitle %s", getClass()));

        element.select("strong").remove();
        element.getElementsByTag("a").remove();

        val text = element.text().replaceAll(PAGE_REGEX, "");

        publicationView.setTitle(text);
    }

    @Part(id = 8, converter = StringAdapter.class, group = GROUP_ID)
    public void onHandleAuthors(String authorsStr) {
        //log.log(Level.INFO, String.format("#onHandleAuthors %s, %s", getClass(), authorsStr));
        publicationView.setAuthors_id(getAuthorIdsByNames(authorsStr.trim().replaceAll("\\s*,\\s*", ",").split(",")));
    }

    @Part(id = 9, converter = UrlAdapter.class, group = GROUP_ID)
    public void onHandleUrl(URL url) {
        //log.log(Level.INFO, String.format("#onHandleUrl %s, %s", getClass(), url));

        if (url == null) {
            log.log(Level.WARNING, "Failed to parse document url");
        } else {
            publicationView.setFile_link(url.toExternalForm());
        }
    }

    @PreHandle(group = GROUP_ID)
    public void prePublication() {
        //log.log(Level.INFO, String.format("#prePublication %s", getClass()));
        this.publicationView = new PublicationView();
        this.publicationView.setStatus(PublicationStatusEnum.ACTIVE);
        this.publicationView.setType(PublicationTypeEnum.ARTICLE);
    }

    @PostHandle(group = GROUP_ID)
    public void postPublication(Page page) {
        //log.log(Level.INFO, String.format("#postPublication %s", getClass()));

        // save parsed page link
        publicationView.setLink(page.getUrl().toExternalForm());

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
                    String nameTemp = fullName
                            .replace("(","")
                            .replace(")","");
                    String[] credentialsArr = nameTemp.split("\\s");// first, middle and last names

                    if (credentialsArr.length < 2) continue;
                    final String initials, lastName;
                    if (credentialsArr.length == 2) {
                        lastName = credentialsArr[0];
                        initials = String.format("%S.", credentialsArr[1].charAt(0));
                    } else {
                        lastName = credentialsArr[0];
                        initials = String.format("%S. %S.", credentialsArr[1].charAt(0), credentialsArr[2].charAt(0));
                    }

                    val foundId = findAuthorId(initials, lastName, fullName);

                    if (cached == null) {
                        // soft reference was released
                        cached = new HashMap<>();
                        fullNameToId = new SoftReference<>(cached);
                    }

                    cached.put(fullName, id = foundId);
                    /*
                    authorView.setLast_name(lastName);
                    authorView.setInitials(initials);
                    AuthorMasterView masterView = new AuthorMasterView();
                    masterView.setLast_name(lastName);
                    masterView.setInitials(initials);
                    authorView.setOriginal(fullName);
                    AuthorMasterEntity master = authorService.findByNameMaster(lastName,initials);
                    AuthorEntity author = authorService.findByOriginal(authorView.getOriginal());
                    if (author == null&& master!=null){
                        authorView.setMaster_id(master.getId());
                        authorService.createAuthor(authorView);
                    }else if (author!=null&&author.getMaster()!=null&&master==null){
                        master = author.getMaster();
                    }else if (author!=null&&author.getMaster()==null&&master==null){
                        id = authorService.createAuthorMaster(masterView);
                        master = authorService.getAuthorMasterById(id);
                        author.setMaster(master);
                        authorService.updateAuthor(author);
                    }else if (author==null&&master==null){
                        id = authorService.createAuthorMaster(masterView);
                        authorView.setMaster_id(id);
                        authorService.createAuthor(authorView);
                    }
                    id =  master.getId();
                    if (fullNameToId.get() != null) {
                        fullNameToId.get().put(fullName, id);
                    }*/
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
