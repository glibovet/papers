package ua.com.papers.exceptions.service_error;

import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Andrii on 12.11.2016.
 */
public class ElasticSearchError extends PapersException {
    @Override
    public int getCode(){
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    @Override
    public String formMessage() {
        return "Elastic Search problems!";
    }
}