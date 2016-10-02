package ua.com.papers.convertors;

import org.elasticsearch.common.recycler.Recycler;
import org.springframework.stereotype.Component;
import ua.com.papers.pojo.entities.AddressEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ua.com.papers.convertors.Fields.Address.*;

/**
 * Created by Andrii on 02.10.2016.
 */
@Component
public class AddressConverter extends Converter<AddressEntity> {
    @Override
    public Map<String, Object> convert(AddressEntity object, Set<String> fields) {
        Map<String,Object> map = new HashMap<>();
        if (fields.contains(ID))
            map.put(ID, object.getId());
        if (fields.contains(COUNTRY))
            map.put(COUNTRY,object.getCountry());
        if (fields.contains(CITY))
            map.put(CITY,object.getCity());
        if (fields.contains(ADDRESS))
            map.put(ADDRESS,object.getAddress());
        return map;
    }
}