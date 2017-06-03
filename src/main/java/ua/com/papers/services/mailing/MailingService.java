package ua.com.papers.services.mailing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.com.papers.exceptions.not_found.NoSuchEntityException;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.EmailTypes;
import ua.com.papers.services.mailing.sendpulse.sendpulse.restapi.Sendpulse;
import ua.com.papers.services.users.IUserService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by KutsykV on 02.11.2015.
 */
@Component
public class MailingService implements IMailingService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private EmailBuilder emailBuilder;
    @Autowired
    private Sendpulse sendpulse;


    public String smtpListEmails(int limit, int offset, String from, String to, String sender, String recipient) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("{\"data\":[");
        boolean start = true;
        try {
            Date fromDate = formatter.parse(from);
            Date toDate = formatter.parse(to);
            while (toDate.after(fromDate)) {
                Date nextDay = new Date(fromDate.getTime() + (1000 * 60 * 60 * 24));
                int batch = 0;
                Map<String, Object> smtpRes = sendpulse.smtpListEmails(50, 0,
                        formatter.format(fromDate),
                        formatter.format(fromDate),
                        sender, recipient);
                while (smtpRes.get("data").toString().length() > 2) {
                    String tmp = smtpRes.get("data").toString();
                    tmp = tmp.substring(1, tmp.length() - 1);
                    if (start) {
                        resultBuilder.append(tmp);
                        start = false;
                    } else {
                        resultBuilder.append(",").append(tmp);
                    }
                    batch++;
                    offset = batch * 50;
                    smtpRes = sendpulse.smtpListEmails(50, offset,
                            formatter.format(fromDate),
                            formatter.format(fromDate),
                            sender, recipient);
                }
                fromDate = nextDay;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        resultBuilder.append("]}");
        return resultBuilder.toString();
    }

    @Override
    @Transactional
    public boolean sendEmailToUser(EmailTypes typeOfEmail, String userEmail, Map<String, String> data, Locale locale) throws NoSuchEntityException {
        String content = emailBuilder.getEmailContent(typeOfEmail, data, locale);
        return send(typeOfEmail, userEmail, content, locale);
    }

    @Override
    public boolean sendEmailToUsers(EmailTypes typeOfEmail, List<String> userEmails, Map<String, String> data, Locale locale) throws NoSuchEntityException {
        for (String user : userEmails) {
            sendEmailToUser(typeOfEmail, user, data, locale);
        }
        return true;
    }

    private boolean send(EmailTypes type, String toEmail, String text, Locale locale) {
        final ExecutorService service;
        final Future<String> task;
        service = Executors.newFixedThreadPool(1);
        String subject = messageSource.getMessage("email.subject." + type.toString(), null, locale);
        task = service.submit(new SenderTask("admin@scisearch.com.ua", toEmail, text, subject));

        return executeSendTask(service, task);
    }

    private boolean executeSendTask(final ExecutorService service, final Future<String> task) {
        try {
            final String str;
            str = task.get(); // this raises ExecutionException if thread dies
            if (str.contains("\"result\":true"))
                return true;
        } catch (final InterruptedException ex) {
            ex.printStackTrace();
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }
        service.shutdownNow();
        return false;
    }

    private String originalEmail(String email) {
        String login = email.substring(0, email.indexOf('@'));
        int indexOfPlus = login.indexOf('+');
        if (indexOfPlus > -1)
            login = login.substring(0, indexOfPlus);
        return login + email.substring(email.indexOf('@'));
    }

    private String defineEmailToSend(UserEntity toUser, Map<String, String> data) {
        String email;
        if (data != null && data.get("email") != null) {
            email = data.get("email");
        } else
            email = originalEmail(toUser.getEmail());
        return email;
    }

    class SenderTask implements Callable<String> {

        String fromEmail;
        String toEmail;
        String text;
        String subject;

        SenderTask() {
        }

        SenderTask(String fromEmail, String toEmail, String text, String subject) {
            this.fromEmail = fromEmail;
            this.toEmail = toEmail;
            this.text = text;
            this.subject = subject;
        }

        public String call() {
            Map<String, Object> fromSender = new HashMap<String, Object>();
            fromSender.put("name", "SciSearch");
            fromSender.put("email", fromEmail);
            ArrayList<Map> toSend = new ArrayList<Map>();
            Map<String, Object> elementto = new HashMap<String, Object>();
            elementto.put("email", toEmail);
            toSend.add(elementto);
            Map<String, Object> emaildata = new HashMap<String, Object>();
            emaildata.put("html", text);
            emaildata.put("text", text);
            emaildata.put("subject", subject);
            emaildata.put("from", fromSender);
            emaildata.put("to", toSend);
            Map<String, Object> result = (Map<String, Object>) sendpulse.smtpSendMail(emaildata);
            return result.toString();
        }
    }


}
