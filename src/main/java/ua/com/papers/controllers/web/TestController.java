package ua.com.papers.controllers.web;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.view.AuthorView;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.crawler.ICrawlerService;
import ua.com.papers.services.crawler.unit.repo.JobEntity;
import ua.com.papers.services.crawler.unit.repo.JobStatus;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import java.sql.Date;
import java.util.Arrays;

/**
 * Created by Максим on 2/2/2017.
 */
@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TestController {

    IPublicationService service;
    IPublisherService publisherService;
    IAuthorService authorService;
    ICrawlerService crawlerService;
    EntityManager entityManager;

    @Autowired
    public TestController(
            IPublicationService service, IPublisherService publisherService,
            IAuthorService authorService, ICrawlerService crawlerService,
            AbstractEntityManagerFactoryBean managerFactoryBean) {
        this.service = service;
        this.publisherService = publisherService;
        this.authorService = authorService;
        this.crawlerService = crawlerService;

        this.entityManager = managerFactoryBean.getNativeEntityManagerFactory().createEntityManager();

        // .createQuery("").setLockMode().getSingleResult()find()getCriteriaBuilder().createQuery().se

        for (int i = 0; i < 10; ++i) {
            entityManager.persist(new JobEntity("www.example.com" + i, new Date(System.currentTimeMillis()), JobStatus.PENDING));
            //jobsRepository.save(new JobEntity("www.example.com" + i, new Date(System.currentTimeMillis()), JobStatus.PENDING));
        }
    }

    @RequestMapping(value = {"/tmp"}, method = RequestMethod.GET)
    public String tmp() {
        //scheduleCrawling.startCrawling();
        // System.out.println(jobsRepository.next());

        try {
            entityManager.getTransaction().begin();

            val q = entityManager.createQuery("SELECT j FROM JobEntity j WHERE j.status = :status", JobEntity.class);

            q.setParameter("status", JobStatus.PROCESSING)
                    .setMaxResults(1)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE);

            try {
                val res = q.getSingleResult();

                System.out.println(res);

               // res.setStatus(JobStatus.ERROR);

                entityManager.merge(res);

                System.out.println("Updated");
            } catch (NoResultException nre) {
                nre.printStackTrace();
            }


            entityManager.getTransaction().commit();

        } catch (Throwable th) {
            th.printStackTrace();
            entityManager.getTransaction().rollback();
        }

        return "redirect:/";
    }

    @RequestMapping(value = {"/start"}, method = RequestMethod.GET)
    public String indexPage() {
        //scheduleCrawling.startCrawling();

        crawlerService.startCrawling();
        return "redirect:/";
    }

    @RequestMapping(value = {"/stop"}, method = RequestMethod.GET)
    public String stopIndexPage() {
        //scheduleCrawling.stopCrawling();
        crawlerService.stopCrawling();
        return "redirect:/";
    }

    @RequestMapping(value = {"/reindex"}, method = RequestMethod.GET)
    public String reIndex() {
        /*crawler.startIndexing(
                composer.asHandlers(),
                IPageIndexer.DEFAULT_CALLBACK
        );*/

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
