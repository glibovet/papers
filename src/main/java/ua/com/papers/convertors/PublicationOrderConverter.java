package ua.com.papers.convertors;

import org.springframework.stereotype.Component;
import ua.com.papers.pojo.entities.PublicationOrderEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static ua.com.papers.convertors.Fields.PublicationOrder.*;

/**
 * Created by Andrii on 20.05.2017.
 */
@Component
public class PublicationOrderConverter extends Converter<PublicationOrderEntity>{
    @Override
    public Map<String, Object> convert(PublicationOrderEntity object, Set<String> fields) {
        Map<String,Object> map = new HashMap<>();
        if (fields.contains(ID))
            map.put(ID, object.getId());
        if (fields.contains(EMAIL))
            map.put(EMAIL,object.getEmail());
        if (fields.contains(REASON))
            map.put(REASON,object.getReason());
        if (fields.contains(ANSWER))
            map.put(ANSWER,object.getAnswer());
        if (fields.contains(STATUS))
            map.put(STATUS,object.getStatus());
        if (fields.contains(PUBLICATION_ID)) {
            if (object.getPublication() != null)
                map.put(PUBLICATION_ID, object.getPublication().getId());
            else
                map.put(PUBLICATION_ID,null);
        }
        if (fields.contains(DATE))
            map.put(DATE,object.getDateCreated());
        return map;
    }
}
