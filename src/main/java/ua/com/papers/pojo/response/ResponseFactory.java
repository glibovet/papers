package ua.com.papers.pojo.response;

import org.springframework.stereotype.Component;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
@Component
public class ResponseFactory {

    public <T> Response<T> get(T t){
        Response<T> response = new Response<T>();
        response.setResult(t);
        return response;
    }

}
