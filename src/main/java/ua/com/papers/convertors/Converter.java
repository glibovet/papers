package ua.com.papers.convertors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by oleh_kurpiak on 07.09.2016.
 */
public abstract class Converter<T> {

    public abstract Map<String, Object> convert(T object, Set<String> fields);

    public List<Map<String, Object>> convert(List<T> objects, Set<String> fields){
        List<Map<String, Object>> result = new ArrayList<>();
        for(T t : objects)
            result.add(convert(t, fields));

        return result;
    }

}
