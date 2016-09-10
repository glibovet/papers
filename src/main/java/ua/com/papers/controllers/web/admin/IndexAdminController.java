package ua.com.papers.controllers.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.RolesEnum;
import ua.com.papers.services.utils.SessionUtils;

import java.util.Locale;

/**
 * Created by Andrii on 26.08.2016.
 */
@Controller
@RequestMapping("/admin/")
public class IndexAdminController {

    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String indexPage(Model model){
        String namechinese = messageSource.getMessage("error.user.email.require",
                null,
                LocaleContextHolder.getLocale());
        return "/admin/index";
    }
}
