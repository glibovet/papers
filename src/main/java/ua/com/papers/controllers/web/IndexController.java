package ua.com.papers.controllers.web;

import com.dropbox.core.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.com.papers.exceptions.service_error.StorageException;
import ua.com.papers.pojo.storage.FileData;
import ua.com.papers.pojo.storage.FileItem;
import ua.com.papers.storage.IStorage;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

/**
 * Created by Andrii on 27.07.2016.
 */
@Controller
public class IndexController {

    @Autowired
    private IStorage storage;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String indexPage(Model model, Principal principal){
        try {
            byte[] bytes = IOUtil.slurp(new FileInputStream(new File("E:\\text.txt")), 0);
            storage.upload(bytes, "root.txt", null);
            storage.upload(bytes, "sub.txt", "/sub/asd");

            List<FileItem> before = storage.listFiles(null);
            System.out.println(before);
            storage.delete("root.txt", "/");
            List<FileItem> after = storage.listFiles(null);
            System.out.println(after);
            if(before.size() - after.size() == 1)
                System.out.println("delete ok");
            else
                System.out.println("delete fail");

            System.out.println(storage.listFiles("sub"));
            System.out.println(storage.listFiles("sub/asd"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }

        return "index/index";
    }

    @PreAuthorize("isAnonymous()")
    @RequestMapping(value = "/sign_up", method = RequestMethod.GET)
    public String signUp(){
        return "auth/sign_up";
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public void getFile(HttpServletResponse response){
        try {
            byte[] bytes = IOUtil.slurp(new FileInputStream(new File("E:\\1.jpg")), 0);
            storage.upload(bytes, "asd.jpg", null);
            FileData data = storage.download(response.getOutputStream(), "asd", null);
            System.out.println(data.name);
            System.out.println(data.size);

            data = storage.download(response.getOutputStream(), "aawgsd", null);
            System.out.println(data.name);
            System.out.println(data.size);
        } catch (StorageException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
