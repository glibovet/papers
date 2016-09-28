package ua.com.papers.convertors;

import org.springframework.stereotype.Component;
import ua.com.papers.pojo.entities.AuthorEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ua.com.papers.convertors.Fields.Author.*;

/**
 * Created by Andrii on 28.09.2016.
 */
@Component
public class AuthorConverter extends Converter<AuthorEntity>{
    @Override
    public Map<String, Object> convert(AuthorEntity object, Set<String> fields) {
        Map<String,Object> map = new HashMap<>();
        if (fields.contains(ID))
            map.put(ID, object.getId());
        if (fields.contains(LAST_NAME))
            map.put(LAST_NAME,object.getLastName());
        if (fields.contains(INITIALS))
            map.put(INITIALS,object.getInitials());
        if (fields.contains(ORIGINAL))
            map.put(ORIGINAL,object.getOriginal());
        if (fields.contains(MASTER)&&object.getMaster()!=null)
            map.put(MASTER,object.getMaster().getId());
        return map;
    }
}
