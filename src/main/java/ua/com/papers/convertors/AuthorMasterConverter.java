package ua.com.papers.convertors;

import org.springframework.stereotype.Component;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;
import ua.com.papers.pojo.entities.PublicationEntity;

import java.util.*;

import static ua.com.papers.convertors.Fields.AuthorMaster.*;

/**
 * Created by Andrii on 28.09.2016.
 */
@Component
public class AuthorMasterConverter extends Converter<AuthorMasterEntity> {
    @Override
    public Map<String, Object> convert(AuthorMasterEntity object, Set<String> fields) {
        Map<String,Object> map = new HashMap<>();
        if (fields.contains(ID))
            map.put(ID, object.getId());
        if (fields.contains(LAST_NAME))
            map.put(LAST_NAME,object.getLastName());
        if (fields.contains(INITIALS))
            map.put(INITIALS,object.getInitials());
        if (fields.contains(AUTHORS)&&object.getAuthors()!=null&&object.getAuthors().size()>0){
            List<Integer> ids = new ArrayList<>();
            for (AuthorEntity author:object.getAuthors())
                ids.add(author.getId());
            map.put(AUTHORS,ids);
        }

        if(fields.contains(PUBLICATIONS_COUNT)) {
            if (object.getPublications() != null) {
                map.put(PUBLICATIONS_COUNT, object.getPublications().size());
            } else {
                map.put(PUBLICATIONS_COUNT, 0);
            }
        }
        if (fields.contains(PUBLICATIONS_ID_LIST)) {
            if (object.getPublications() != null) {
                List<Integer> ids = new ArrayList<>();
                for (PublicationEntity entity : object.getPublications()) {
                    ids.add(entity.getId());
                }
                map.put(PUBLICATIONS_ID_LIST, ids);
            } else {
                map.put(PUBLICATIONS_ID_LIST, null);
            }
        }
        return map;
    }
}
