package ua.com.papers.services.mailing;

import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.EmailTypes;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by KutsykV on 24.01.2016.
 */
public interface IMailingService {
    boolean sendEmailToUser(EmailTypes typeOfEmail, String userEmail, Map<String, String> data, Locale locale) throws NoSuchEntityException;
    boolean sendEmailToUsers(EmailTypes typeOfEmail, List<String> users, Map<String, String> data, Locale locale) throws NoSuchEntityException;
    String smtpListEmails(int limit, int offset, String from, String to, String sender, String recipient);
}
