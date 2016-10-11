package ua.com.papers.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by oleh_kurpiak on 11.10.2016.
 */
@Controller
@RequestMapping("/resources")
public class ResourcesController {

    @RequestMapping(value = "/messages/{file}.js", method = RequestMethod.GET)
    public String getMessages(@PathVariable("file") String file,
                              Locale locale,
                              Model model){
        ResourceBundle bundle = ResourceBundle.getBundle(file, locale);
        model.addAttribute("keys", bundle.getKeys());
        model.addAttribute("var", file);
        return "resources/messages";
    }
}
