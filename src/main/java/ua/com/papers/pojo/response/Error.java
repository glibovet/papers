package ua.com.papers.pojo.response;

import java.util.List;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
public class Error {

    private int code;

    private String message;

    private List<String> errors;

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Error(int code, String message, List<String> errors) {
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
