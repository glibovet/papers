package ua.com.papers.exceptions.service_error;

import org.springframework.context.MessageSource;
import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
public class ServiceErrorException extends PapersException {

    public ServiceErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceErrorException(Throwable cause) {
        super(cause);
    }

    protected ServiceErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ServiceErrorException(String message) {
        super(message);
    }

    public ServiceErrorException() {
        this("Internal Server Error");
    }

    @Override
    public int getCode(){
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    @Override
    public String formMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("errors.ServiceErrorException", null, locale);
    }

    @Override
    public List<String> formListErrors(MessageSource messageSource, Locale locale) {
        return null;
    }
}
