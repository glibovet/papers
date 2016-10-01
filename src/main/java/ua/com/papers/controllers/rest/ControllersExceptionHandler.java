package ua.com.papers.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.papers.exceptions.PapersException;
import ua.com.papers.pojo.response.Error;
import ua.com.papers.pojo.response.Response;

import java.util.List;

@ControllerAdvice
public class ControllersExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(PapersException.class)
    public @ResponseBody
    Response handler(PapersException e){
        String locale = LocaleContextHolder.getLocale().getLanguage();
        List<String> errors = e.formListErrors(messageSource, locale);

        Error error = error = new Error(e.getCode(), e.formMessage(), errors);
        Response response = new Response();
        response.setError(error);

        return response;
    }
}
