package ua.com.papers.convertors;

import org.springframework.stereotype.Component;
import ua.com.papers.pojo.entities.PublicationEntity;

import java.util.*;

import static ua.com.papers.convertors.Fields.Publication.*;
/**
 * Created by Andrii on 26.09.2016.
 */
@Component
public class PublicationConverter extends Converter<PublicationEntity> {
    @Override
    public Map<String, Object> convert(PublicationEntity object, Set<String> fields) {
        Map<String, Object> map = new HashMap<>();
        if (fields.contains(ID))
            map.put(ID, object.getId());
        if(fields.contains(TITLE))
            map.put(TITLE,object.getTitle());
        if (fields.contains(ANNOTATION))
            map.put(ANNOTATION,object.getAnnotation());
        if (fields.contains(TYPE))
            map.put(TYPE,object.getType());
        if (fields.contains(LINK))
            map.put(LINK, object.getLink());
        if (fields.contains(STATUS))
            map.put(STATUS,object.getStatus());
        if (fields.contains(PUBLISHER))
            map.put(PUBLISHER,object.getPublisher());
        return map;
    }
}
