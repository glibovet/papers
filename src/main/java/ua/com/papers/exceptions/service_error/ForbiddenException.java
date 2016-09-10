package ua.com.papers.exceptions.service_error;

import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Andrii on 10.09.2016.
 */
public class ForbiddenException extends PapersException {
    @Override
    public int getCode(){
        return HttpServletResponse.SC_FORBIDDEN;
    }

    @Override
    public String formMessage() {
        return "You have no permissions for this method!";
    }
}
