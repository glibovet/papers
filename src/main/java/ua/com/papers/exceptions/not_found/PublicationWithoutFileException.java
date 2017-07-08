package ua.com.papers.exceptions.not_found;

import org.springframework.context.MessageSource;
import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

/**
 * Created by Oleh on 08.07.2017.
 */
public class PublicationWithoutFileException extends PapersException {

    @Override
    public int getCode() {
        return HttpServletResponse.SC_NOT_FOUND;
    }

    @Override
    public String formMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("errors.PublicationWithoutFileException", null, locale);
    }

    @Override
    public List<String> formListErrors(MessageSource messageSource, Locale locale) {
        return null;
    }
}
