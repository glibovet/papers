package ua.com.papers.convertors;

import org.springframework.stereotype.Component;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
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
        if (fields.contains(PUBLISHER)) {
            if (object.getPublisher() != null)
                map.put(PUBLISHER, object.getPublisher().getId());
            else
                map.put(PUBLISHER, null);
        }
        if (fields.contains(IN_INDEX))
            map.put(IN_INDEX,object.isInIndex());
        if (fields.contains(STATUS))
            map.put(STATUS,object.getStatus());
        if (fields.contains(LITERATURE_PARSED))
            map.put(LITERATURE_PARSED, object.isLiteratureParsed());
        if (fields.contains(AUTHORS)&&object.getAuthors()!=null&&object.getAuthors().size()>0){
            List<Integer> ids = new ArrayList<>();
            for (AuthorMasterEntity author:object.getAuthors())
                ids.add(author.getId());
            map.put(AUTHORS,ids);
        }
        if (fields.contains(FILE_LINK))
            map.put(FILE_LINK, object.getFileLink());
        return map;
    }
}
