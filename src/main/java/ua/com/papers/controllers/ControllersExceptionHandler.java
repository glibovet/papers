package ua.com.papers.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.pojo.response.Error;
import ua.com.papers.pojo.response.Response;

import java.util.List;

@ControllerAdvice
public class ControllersExceptionHandler {

    @ExceptionHandler(PapersException.class)
    public @ResponseBody
    Response handler(PapersException e){
        List<String> errors = e.formListErrors();
        Error error = null;
        if(errors == null || errors.isEmpty()){
            error = new Error(e.getCode(), e.formMessage());
        } else {
            error = new Error(e.getCode(), e.formMessage(), e.formListErrors());
        }

        Response response = new Response();
        response.setError(error);

        return response;
    }
}
