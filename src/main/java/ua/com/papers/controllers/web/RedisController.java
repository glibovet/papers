package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.services.redis.IRedisService;
import ua.com.papers.services.utils.SessionUtils;

@Controller
public class RedisController {

    @Autowired
    private IRedisService redisService;

    @Autowired
    private SessionUtils sessionUtils;

    @RequestMapping(value = "/redis/register-clicked-publication/{publicationId}",
            method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public void registerClickedPublication(@PathVariable Integer publicationId){
        UserEntity user = this.sessionUtils.getCurrentUser();
        if(user != null) {
            this.redisService.updateKey(user.getId(), publicationId, "clicked");
        }
    }

}
