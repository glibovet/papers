package ua.com.papers.crawler;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.vo.PageID;
import ua.com.papers.crawler.settings.*;
import ua.com.papers.crawler.util.ICrawlerFactory;

import javax.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

public class Tester {

    public static void main(String[] args) throws Exception {

        final AbstractApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:testContext.xml");

        final ICrawlerFactory factory = context.getBean(ICrawlerFactory.class);

        AnalyzeTemplate.Builder analyze1 = new AnalyzeTemplate
                .Builder("a[href^='/jenkins/']", 60);
        //.addAction(AnalyzeTemplate.Action.REDIRECT);

        AnalyzeTemplate.Builder analyze2 = new AnalyzeTemplate
                .Builder("body > div.main > div.container > div > div.row", 60);

        FormatTemplate.Builder format1 = new FormatTemplate
                .Builder("body > div.main > div.container > div > div.row > div.content > div > h1", 1);

        FormatTemplate.Builder format2 = new FormatTemplate
                .Builder("body > div.main > div.container > div > div.row > div.content > div > p", 2);

        PageSetting pageSetting = PageSetting.builder()
                .id(new PageID(1))
                .analyzeTemplate(analyze1.build())
                .analyzeTemplate(analyze2.build())
                .formatTemplate(format1.build())
                .formatTemplate(format2.build())
                .selectSetting(UrlSelectSetting.builder().cssSelector("a[href^='/jenkins/']").attrName("href").build())
                .build();

        Settings settings = new Settings.Builder(
                new SchedulerSetting(),
                Collections.singletonList(
                        new URL("http://www.tutorialspoint.com/jenkins/")
                ),
                Collections.singletonList(
                        pageSetting
                )
        ).build();

        final ICrawler crawler = factory.create(settings);
        final BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Максим\\Desktop\\log.txt", false));

        crawler.start(new ICrawler.ICallback() {

            @Override
            public void onStart() {
                System.out.println("On start");
            }

            @Override
            public void onUrlEntered(@NotNull URL url) {
                System.out.println("On url entered " + url);
            }

            @Override
            public void onPageRejected(@NotNull Page page) {
          //      System.out.println("On page rejected " + page);
            }

            @Override
            public void onStop() {
                System.out.println("On stop");
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onException(@NotNull Throwable th) {
                System.out.println("On exception " + th);
            }

            @Override
            public void onPageAccepted(@NotNull Page page) {
                try {
                    writer.write("*******************************************************************************");
                    writer.newLine();
                    writer.write(page.toString());
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
               // System.out.println("Page accepted " + page);
            }
        }, Collections.singletonList(new HandlerDemo()));
    }

}