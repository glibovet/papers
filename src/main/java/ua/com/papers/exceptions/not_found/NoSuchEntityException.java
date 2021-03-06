package ua.com.papers.exceptions.not_found;

import org.springframework.context.MessageSource;
import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;

/**
 * Created by Andrii on 18.08.2016.
 */
public class NoSuchEntityException extends PapersException {

    private static final String DEFAULT_MESSAGE = "No entity of type '%s' and params %s";
    private static final String NO_ENTITY_OF_TYPE = "No entity of type '%s'";

    public NoSuchEntityException(String className){
        super(String.format(NO_ENTITY_OF_TYPE , className));
    }

    public NoSuchEntityException(String className, String params){
        super(String.format(DEFAULT_MESSAGE, className, params));
    }

    public int getCode(){
        return HttpServletResponse.SC_NOT_FOUND;
    }

    @Override
    public String formMessage(MessageSource messageSource, Locale locale) {
        return getMessage();
    }

    @Override
    public List<String> formListErrors(MessageSource messageSource, Locale locale) {
        return null;
    }
}
