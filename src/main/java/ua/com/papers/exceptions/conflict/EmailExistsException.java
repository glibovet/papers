package ua.com.papers.exceptions.conflict;

import ua.com.papers.exceptions.PapersException;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
public class EmailExistsException extends PapersException {

    @Override
    public int getCode() {
        return HttpServletResponse.SC_CONFLICT;
    }

    @Override
    public String formMessage(){
        return "User with this email already exists";
    }
}
