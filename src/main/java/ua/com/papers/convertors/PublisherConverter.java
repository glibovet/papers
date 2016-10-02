package ua.com.papers.convertors;

import org.springframework.stereotype.Component;
import ua.com.papers.pojo.entities.PublisherEntity;

import static ua.com.papers.convertors.Fields.Publisher.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Andrii on 02.10.2016.
 */
@Component
public class PublisherConverter extends Converter<PublisherEntity>{
    @Override
    public Map<String, Object> convert(PublisherEntity object, Set<String> fields) {
        Map<String,Object> map = new HashMap<>();
        if(fields.contains(ID))
            map.put(ID,object.getId());
        if (fields.contains(TITLE))
            map.put(TITLE,object.getTitle());
        if (fields.contains(DESCRIPTION))
            map.put(DESCRIPTION,object.getDescription());
        if (fields.contains(URL))
            map.put(URL,object.getUrl());
        if (fields.contains(CONTACTS))
            map.put(CONTACTS,object.getContacts());
        if (fields.contains(ADDRESS)&&object.getAddress()!=null)
            map.put(ADDRESS, object.getAddress().getId());
        return map;
    }
}