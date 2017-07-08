package ua.com.papers.exceptions.bad_request;

import org.springframework.context.MessageSource;
import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

/**
 * Created by oleh_kurpiak on 14.10.2016.
 */
public class WrongRestrictionException extends PapersException {

    public WrongRestrictionException(){
        super("Wrong restriction");
    }

    @Override
    public int getCode() {
        return HttpServletResponse.SC_BAD_REQUEST;
    }

    @Override
    public String formMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("errors.WrongRestrictionException", null, locale);
    }

    @Override
    public List<String> formListErrors(MessageSource messageSource, Locale locale) {
        return null;
    }

}
