package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.crawler.core.creator.ICreator;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.schedule.ICrawlerManager;
import ua.com.papers.crawler.test.MainComposer;
import ua.com.papers.criteria.Criteria;
import ua.com.papers.criteria.impl.UserCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.EmailTypes;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.pojo.view.AuthorView;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.mailing.IMailingService;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.services.users.IUserService;

import javax.validation.constraints.NotNull;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Максим on 2/2/2017.
 */
@Controller
public class TestController {

    private final IPublicationService service;
    private final IPublisherService publisherService;
    private final IAuthorService authorService;
    private final MainComposer composer;
    private final IMailingService mailingService;
    private final IUserService userService;

    ICrawlerManager crawler;

    @Autowired
    public TestController(
            IPublicationService service, IPublisherService publisherService,
            IAuthorService authorService, MainComposer composer, ICreator creator,
            IMailingService mailingService, IUserService userService) {
        this.service = service;
        this.publisherService = publisherService;
        this.authorService = authorService;
        this.composer = composer;
        this.crawler = creator.create();
        this.mailingService = mailingService;
        this.userService = userService;
    }

    @RequestMapping(value = {"/crawl"}, method = RequestMethod.GET)
    public String indexPage() {

        crawler.startCrawling(
                composer.asHandlers(),
                new ICrawler.Callback() {
                    @Override
                    public void onPageAccepted(Page page) {

                    }

                    @Override
                    public void onStop() {
                        System.out.println("FINISH CRAWLING");

                        try {
                            UserCriteria criteria = new UserCriteria(null);
                            criteria.setRoles(Arrays.asList(RolesEnum.admin, RolesEnum.moderator));
                            criteria.setActive(true);

                            List<UserEntity> users = userService.getUsers(criteria);

                            for (UserEntity user : users) {
                                mailingService.sendEmailToUser(
                                        EmailTypes.crawling_finish,
                                        user.getEmail(),
                                        null,
                                        new Locale("uk")
                                );
                            }
                        } catch (WrongRestrictionException | NoSuchEntityException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        return "redirect:/";
    }

    @RequestMapping(value = {"/stop"}, method = RequestMethod.GET)
    public String stopIndexPage() {
        crawler.stopCrawling();
        crawler.stopIndexing();

        return "redirect:/";
    }

    @RequestMapping(value = {"/reindex"}, method = RequestMethod.GET)
    public String reIndex() {
        crawler.startIndexing(
                composer.asHandlers(),
                IPageIndexer.DEFAULT_CALLBACK
        );

        return "redirect:/";
    }

    @RequestMapping(value = {"/crawl1"}, method = RequestMethod.GET)
    public String indexPage1() throws WrongRestrictionException {
        try {

          //  AuthorMasterView masterView = new AuthorMasterView();

           // masterView.setInitials("initials");
           // masterView.setLast_name("last name");

           // int masterId = authorService.createAuthorMaster(masterView);

            AuthorView authorView = new AuthorView();

            authorView.setMaster_id(1);
            authorView.setLast_name("last name1");
            authorView.setOriginal("original");

            int authorId = authorService.createAuthor(authorView);

            PublisherView publisherView = new PublisherView();

            publisherView.setTitle("title1");
       //     publisherView.setAddress(1);
       //     publisherView.setContacts("contacts");
       //     publisherView.setDescription("description");
            publisherView.setUrl("www.example.com1");

            int pubId = publisherService.createPublisher(publisherView);

            PublicationView view = new PublicationView();

            view.setTitle("Title1");
            view.setAnnotation("Annotation1");
            view.setLink("www.example.com1");
            view.setStatus(PublicationStatusEnum.ACTIVE);
            view.setType(PublicationTypeEnum.ARTICLE);
            view.setPublisher_id(pubId);
            view.setAuthors_id(Arrays.asList(authorId));

            service.createPublication(view);
        } catch (ServiceErrorException e) {
            e.printStackTrace();
        } catch (NoSuchEntityException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace();
        }
        System.out.println("On created");
        return "index/index";
    }

}
