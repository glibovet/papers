package ua.com.papers.exceptions.service_error;

import org.springframework.context.MessageSource;
import ua.com.papers.exceptions.PapersException;

import javax.validation.ConstraintViolation;
import java.util.*;

/**
 * Created by Andrii on 10.09.2016.
 */
public class ValidationException extends PapersException {

    private Set<String> codes;

    public <T> ValidationException(String className, Set<ConstraintViolation<T>> violations){
        super(String.format(DEFAULT_MESSAGE, className));
        this.codes = new HashSet<>();
        if(violations != null) {
            for (ConstraintViolation<T> t : violations) {
                codes.add(t.getMessage());
            }
        }
    }

    public ValidationException(String className, String code){
        super(String.format(DEFAULT_MESSAGE, className));
        this.codes = new HashSet<>();
        this.codes.add(code);
    }

    @Override
    public String formMessage(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("errors.ValidationException", null, locale);
    }

    @Override
    public List<String> formListErrors(MessageSource messageSource, Locale locale) {
        List<String> result = new ArrayList<String>();
        for(String code : codes){
            result.add(messageSource.getMessage(code, null, locale));
        }

        return result;
    }

    private static final String DEFAULT_MESSAGE = "Entity of type %s got violations";

}
