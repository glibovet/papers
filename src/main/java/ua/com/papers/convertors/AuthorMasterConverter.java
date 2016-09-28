package ua.com.papers.convertors;

import org.springframework.stereotype.Component;
import ua.com.papers.pojo.entities.AuthorEntity;
import ua.com.papers.pojo.entities.AuthorMasterEntity;

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
        return map;
    }
}
