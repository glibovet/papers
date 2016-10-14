package ua.com.papers.exceptions.bad_request;

import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;

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

}
