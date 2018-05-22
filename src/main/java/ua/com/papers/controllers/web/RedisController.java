package ua.com.papers.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.papers.pojo.dto.search.PublicationDTO;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.services.elastic.IElasticSearch;
import ua.com.papers.services.redis.IRedisService;
import ua.com.papers.services.utils.SessionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
