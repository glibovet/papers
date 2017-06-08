package ua.com.papers.exceptions.service_error;

import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Andrii on 12.11.2016.
 */
public class ElasticSearchError extends PapersException {

    private String message;

    public ElasticSearchError(String message) {
        super(message);
        this.message = message;
    }

    public ElasticSearchError() {
        this("Elastic Search problems!");
    }

    @Override
    public int getCode(){
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    @Override
    public String formMessage() {
        return message;
    }
}
