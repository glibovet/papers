package ua.com.papers.services.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ua.com.papers.crawler.core.factory.ICrawlerFactory;
import ua.com.papers.crawler.core.main.ICrawler;
import ua.com.papers.criteria.impl.UserCriteria;
import ua.com.papers.exceptions.bad_request.WrongRestrictionException;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.EmailTypes;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.services.crawler.MainComposer;
import ua.com.papers.services.mailing.IMailingService;
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

    private ICrawler crawler;

    @Autowired
    public ScheduleCrawling(@Qualifier("nbuvFactory") ICrawlerFactory creator) {
        this.crawler = creator.create();
    }

    public void startCrawling() {
        crawler.start(
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

    public void stopCrawling() {
        crawler.stop();
    }

    private List<UserEntity> admins() throws NoSuchEntityException, WrongRestrictionException {
        UserCriteria criteria = new UserCriteria(null);
        criteria.setRoles(Arrays.asList(RolesEnum.admin, RolesEnum.moderator));
        criteria.setActive(true);

        return userService.getUsers(criteria);
    }
}
