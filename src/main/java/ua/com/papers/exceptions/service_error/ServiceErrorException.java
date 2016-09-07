package ua.com.papers.exceptions.service_error;

import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
public class ServiceErrorException extends PapersException {

    @Override
    public int getCode(){
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    @Override
    public String formMessage() {
        return "Internal Server Error";
    }
}
