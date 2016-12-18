package ua.com.papers.crawler;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.settings.*;
import ua.com.papers.crawler.util.ICrawlerFactory;

import javax.validation.constraints.NotNull;
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

        PageSetting.Builder pageSettBuilder = new PageSetting.Builder(1)
                .addAnalyzeTemplate(analyze1.build())
                .addAnalyzeTemplate(analyze2.build())
                .addFormatTemplate(format1.build())
                .addFormatTemplate(format2.build());

        Settings settings = new Settings.Builder(
                new SchedulerSetting(),
                Collections.singletonList(new URL("http://www.tutorialspoint.com/jenkins/")),
                Collections.singletonList(pageSettBuilder.build())
        ).build();

        final ICrawler crawler = factory.create(settings);

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
                System.out.println("On page rejected " + page);
            }

            @Override
            public void onStop() {
                System.out.println("On stop");
            }

            @Override
            public void onException(@NotNull Throwable th) {
                System.out.println("On exception " + th);
            }

            @Override
            public void onPageAccepted(@NotNull Page page) {
                System.out.println("Page accepted " + page);
            }
        }, Collections.emptyList());
    }

}
