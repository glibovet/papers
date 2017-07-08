package ua.com.papers.exceptions;

import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
public abstract class PapersException extends Exception {

    private int code;

    public PapersException(){
        this("PapersException");
    }

    public PapersException(String message){
        super(message);
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * override this method to create custom message for users
     *
     * @return converted message
     */
    public abstract String formMessage(MessageSource messageSource, Locale locale);

    /**
     * override this method to create a list of errors for user
     *
     * return null to show that exception has no list of errors
     *
     * @return list of errors
     */
    public abstract List<String> formListErrors(MessageSource messageSource, Locale locale);
}
