package ua.com.papers.exceptions.bad_request;

import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by oleh_kurpiak on 11.09.2016.
 */
public class WrongPasswordException extends PapersException {

    public WrongPasswordException(){
        super("Wrong password");
    }

    @Override
    public int getCode() {
        return HttpServletResponse.SC_BAD_REQUEST;
    }
}
