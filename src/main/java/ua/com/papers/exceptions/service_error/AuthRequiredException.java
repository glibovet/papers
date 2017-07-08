package ua.com.papers.exceptions.service_error;

import org.springframework.context.MessageSource;
import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andrii on 10.09.2016.
 */
public class AuthRequiredException extends PapersException {

    @Override
    public int getCode(){
        return HttpServletResponse.SC_UNAUTHORIZED;
    }

    @Override
    public String formMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("errors.AuthRequiredException", null, locale);
    }

    @Override
    public List<String> formListErrors(MessageSource messageSource, Locale locale) {
        return null;
    }
}
