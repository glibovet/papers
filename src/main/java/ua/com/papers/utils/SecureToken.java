package ua.com.papers.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Oleh on 11.06.2017.
 */
public class SecureToken {

    private Map<String, Object> data = new HashMap<>();

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void add(String key, Object value) {
        data.put(key, value);
    }

    static final String SUBJECT = "secure_token";
    static final String DATA = "data";
}
