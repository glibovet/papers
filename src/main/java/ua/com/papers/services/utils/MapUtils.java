package ua.com.papers.services.utils;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MapUtils {

    private <K,V extends Comparable<? super V>> List<Map.Entry<K,V>> getSortedList(Map<K,V> map, String order) {
        List<Map.Entry<K,V>> list = new LinkedList<Map.Entry<K,V>>(map.entrySet());

        list.sort(new Comparator<Map.Entry<K,V>>()
        {
            public int compare(Map.Entry<K,V> o1, Map.Entry<K,V> o2) {
                if (order.equals("ASC")) {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        return list;
    }

    public <K,V extends Comparable<? super V>> Map<K,V> getSortedMap(Map<K,V> map, String order) {

        // 1. Sort list based on values
        List<Map.Entry<K,V>> list = this.getSortedList(map, order);

        // 2. Create map based on sorted list
        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
        for (Map.Entry<K,V> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
