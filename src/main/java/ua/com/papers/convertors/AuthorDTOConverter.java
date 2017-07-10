package ua.com.papers.convertors;

import org.springframework.stereotype.Component;
import ua.com.papers.pojo.dto.search.AuthorDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ua.com.papers.convertors.Fields.AuthorDTO.*;

/**
 * Created by Andrii on 10.07.2017.
 */
@Component
public class AuthorDTOConverter extends Converter<AuthorDTO> {
    @Override
    public Map<String, Object> convert(AuthorDTO object, Set<String> fields) {
        Map<String,Object> map = new HashMap<>();
        if (fields.contains(ID))
            map.put(ID, object.getId());
        if (fields.contains(NAME))
            map.put(NAME, object.getName());
        if (fields.contains(INITIALS))
            map.put(INITIALS,object.getInitials());
        if(fields.contains(PUBLICATIONS_COUNT))
            map.put(PUBLICATIONS_COUNT,object.getPublicationCount());
        if (fields.contains(PUBLICATIONS_ID_LIST)&&object.getPublicationIds()!=null&&object.getPublicationIds().size()>0)
            map.put(PUBLICATIONS_ID_LIST,object.getPublicationIds());
        return map;
    }
}
