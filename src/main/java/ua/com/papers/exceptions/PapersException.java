package ua.com.papers.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
public class PapersException extends Exception {

    private int code;

    public PapersException(){
        this("PapersException");
    }

    public PapersException(String message){
        super(message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * override this method to create custom messages for users
     * @return converted message
     */
    public String formMessage(){
        return getMessage();
    }

    /**
     * override this method to create a list of errors for user
     * @return list of errors
     */
    public List<String> formListErrors(){
        return null;
    }
}
