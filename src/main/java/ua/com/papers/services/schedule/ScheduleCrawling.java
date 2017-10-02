package ua.com.papers.services.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.creator.ICreator;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.schedule.ICrawlerManager;
import ua.com.papers.crawler.test.MainComposer;
import ua.com.papers.criteria.impl.UserCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.EmailTypes;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.services.authors.IAuthorService;
import ua.com.papers.services.mailing.IMailingService;
import ua.com.papers.services.publications.IPublicationService;
import ua.com.papers.services.publisher.IPublisherService;
import ua.com.papers.services.users.IUserService;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oleh on 09.07.2017.
 */
@Component
public class ScheduleCrawling {

    @Autowired
    private MainComposer composer;
    @Autowired
    private IMailingService mailingService;
    @Autowired
    private IUserService userService;

    private ICrawlerManager crawler;

    @Autowired
    public ScheduleCrawling(ICreator creator) {
        this.crawler = creator.create();
    }

    public void crawl() {
        crawler.startCrawling(
                composer.asHandlers(),
                new ICrawler.Callback() {
                    @Override
                    public void onStart() {
                        try {
                            List<UserEntity> admins = admins();

                            for (UserEntity admin : admins) {
                                mailingService.sendEmailToUser(
                                        EmailTypes.crawling_start,
                                        admin.getEmail(),
                                        null,
                                        new Locale("uk")
                                );
                            }
                        } catch (WrongRestrictionException | NoSuchEntityException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onStop() {
                        try {
                            List<UserEntity> admins = admins();

                            for (UserEntity admin : admins) {
                                mailingService.sendEmailToUser(
                                        EmailTypes.crawling_finish,
                                        admin.getEmail(),
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
    }

    private List<UserEntity> admins() throws NoSuchEntityException, WrongRestrictionException {
        UserCriteria criteria = new UserCriteria(null);
        criteria.setRoles(Arrays.asList(RolesEnum.admin, RolesEnum.moderator));
        criteria.setActive(true);

        return userService.getUsers(criteria);
    }
}
