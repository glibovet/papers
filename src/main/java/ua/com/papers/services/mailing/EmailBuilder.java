package ua.com.papers.services.mailing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.com.papers.pojo.entities.UserEntity;
import ua.com.papers.pojo.enums.EmailTypes;
import ua.com.papers.services.users.IUserService;

import java.io.*;
import java.util.Locale;
import java.util.Map;

/**
 * Created by KutsykV on 24.01.2016.
 */
@Component
public class EmailBuilder {

    @Value("${mail.host}")
    private String host;

    public String getEmailContent(EmailTypes typeOfEmail, Map<String, String> data, Locale locale) {
        try {
            String content = readResourceText("email/" + locale.getLanguage() + "/email." + typeOfEmail.toString() + ".html");

            if (typeOfEmail == EmailTypes.approve_publication_order) {
                return formApprovePublicationOrder(data, content);
            } else if (typeOfEmail == EmailTypes.reject_publication_order) {
                return formRejectPublicationOrder(data, content);
            } else if (typeOfEmail == EmailTypes.crawling_start || typeOfEmail == EmailTypes.crawling_finish) {
                return content;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String formRejectPublicationOrder(Map<String, String> data, String content) {
        content = content.replaceAll("REJECT_REASON",data.get("REJECT_REASON"));
        return content;
    }

    private String formApprovePublicationOrder(Map<String, String> data, String content) {
        content = content.replaceAll("PUBLICATION_LINK",data.get("PUBLICATION_LINK"));
        return content;
    }

    private String readResourceText(String resourceName) throws IOException {
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            StringBuilder content = new StringBuilder();
            Resource resource = new ClassPathResource(resourceName);
            fileInputStream = new FileInputStream(resource.getFile().getAbsolutePath());
            inputStreamReader = new InputStreamReader(fileInputStream, "UTF8");
            reader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            return content.toString();
        } finally {
            close(reader);
            close(inputStreamReader);
            close(fileInputStream);
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) { }
        }
    }

}
