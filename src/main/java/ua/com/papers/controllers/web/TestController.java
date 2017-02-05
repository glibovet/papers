package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.exceptions.service_error.ServiceErrorException;
import ua.com.papers.exceptions.service_error.ValidationException;
import ua.com.papers.pojo.enums.PublicationStatusEnum;
import ua.com.papers.pojo.enums.PublicationTypeEnum;
import ua.com.papers.pojo.view.AuthorMasterView;
import ua.com.papers.pojo.view.AuthorView;
import ua.com.papers.pojo.view.PublicationView;
import ua.com.papers.pojo.view.PublisherView;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;

import java.util.Arrays;

/**
 * Created by Максим on 2/2/2017.
 */
@Controller
public class TestController {

    private final IPublicationService service;
    private final IPublisherService publisherService;
    private final IAuthorService authorService;

    @Autowired
    public TestController(IPublicationService service, IPublisherService publisherService, IAuthorService authorService) {
        this.service = service;
        this.publisherService = publisherService;
        this.authorService = authorService;
    }

    @RequestMapping(value = {"/crawl"}, method = RequestMethod.GET)
    public String indexPage(){
        try {

            AuthorMasterView masterView = new AuthorMasterView();

            masterView.setInitials("initials");
            masterView.setLast_name("last name");

            int masterId = authorService.createAuthorMaster(masterView);

            AuthorView authorView = new AuthorView();

            authorView.setMaster_id(masterId);
            authorView.setLast_name("last name");
            authorView.setOriginal("original");

            int authorId = authorService.createAuthor(authorView);

            PublisherView publisherView = new PublisherView();

            publisherView.setTitle("title");
       //     publisherView.setAddress(1);
            publisherView.setContacts("contacts");
            publisherView.setDescription("description");
            publisherView.setUrl("www.example.com");

            int pubId = publisherService.createPublisher(publisherView);

            PublicationView view = new PublicationView();

            view.setTitle("Title");
            view.setAnnotation("Annotation");
            view.setLink("www.example.com");
            view.setStatus(PublicationStatusEnum.ACTIVE);
            view.setType(PublicationTypeEnum.ARTICLE);
            view.setPublisherId(pubId);
            view.setAuthorsId(Arrays.asList(authorId));

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
