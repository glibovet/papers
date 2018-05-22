package ua.com.papers.services.redis;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.papers.pojo.dto.search.PublicationDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RedisServiceImpl implements IRedisService {

    @Autowired
    private RedisClient redisClient;

    public void updateKey(Integer userId, Integer publicationId, String action) {
        try {
            // keys examples -  userEmail:shown:publicationId or userEmail:clicked:publicationId
            RedisConnection<String, String> connection = redisClient.connect();

            String key = userId + ":" +  action + ":" + publicationId;
            String value = connection.get(key);
            if(value != null) {
                Integer newValue = Integer.parseInt(value) + 1;
                connection.set(key, Integer.toString(newValue));
            } else {
                connection.set(key, Integer.toString(1));
            }

            connection.close();
        } catch(RedisConnectionException e) {
        }

    }

    public Map<Integer, Double> getCTRMap(Integer userId) {
        Map<Integer, Double> hm = new HashMap<>();
        try {
            RedisConnection<String, String> connection = redisClient.connect();
            List<String> listShown = connection.keys(userId + ":shown:*");
            for(String shownKey : listShown) {
                // possible keys are -  userId:shown:publicationId / userId:clicked:publicationId
                String[] arr = shownKey.split(":");
                int publicationId = Integer.parseInt(arr[2]);
                // generate clicked key
                String clickedKey = userId + ":clicked:" + publicationId;

                String shown = connection.get(shownKey);
                String clicked = connection.get(clickedKey);
                if(shown != null && clicked != null) {
                    Double value = Double.parseDouble(clicked) / Double.parseDouble(shown) * 100;
                    hm.put(publicationId, value);
                }
            }

            connection.close();
        } catch(RedisConnectionException e) {
        }

        return hm;
    }

    public void registerShownPublications(Integer userId, List<PublicationDTO> publications) {
        for(PublicationDTO item : publications) {
            this.updateKey(userId, item.getId(), "shown");
        }
    }
}
