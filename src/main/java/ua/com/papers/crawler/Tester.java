package ua.com.papers.crawler;

import ua.com.papers.crawler.core.creator.ICreator;
import ua.com.papers.crawler.core.creator.xml.XmlCreator;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.bo.Page;
import ua.com.papers.crawler.core.domain.schedule.ICrawlerManager;

import javax.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

public class Tester {

    public static void main(String[] args) throws Exception {

        //System.out.println(new File("src/main/resources/crawler/crawler-settings.xml").exists());

       /* final AbstractApplicationContext context =
                new ClassPathXmlApplicationContext("classpath:testContext.xml");

        final ICrawlerFactory factory = context.getBean(ICrawlerFactory.class);

        PageSetting pageSetting = PageSetting.builder()
                .id(new PageID(1))
                .minWeight(20)
                .analyzeTemplate(new AnalyzeTemplate("a[href^='/jenkins/']", 1))
                .analyzeTemplate(new AnalyzeTemplate("body > div.main > div.container > div > div.row", 1))
                .formatTemplate(new FormatTemplate(1, "body > div.main > div.container > div > div.row > div.content > div > h1"))
                .formatTemplate(new FormatTemplate(2, "body > div.main > div.container > div > div.row > div.content > div > p"))
                // selects article's images
                .formatTemplate(new FormatTemplate(3, "body > div.main > div.container > div > div.row > div.content img[src]"))
                .selectSetting(new UrlSelectSetting("a[href^='/jenkins/']", "href"))
                .build();

        val oneMinute = 2 * 1_000L;

        Settings settings = Settings.builder()
                .schedulerSetting(
                        SchedulerSetting.builder()
                                .executorService(Executors.newScheduledThreadPool(2))
                                .indexDelay(oneMinute)
                                .allowIndex(true)
                                .build()
                )
                .startUrl(new URL("https://www.tutorialspoint.com/jenkins"))
                .pageSetting(pageSetting)
                .build();*/

        ICreator creator = new XmlCreator("src/main/resources/crawler/crawler-settings.xml");

        final ICrawlerManager scheduler = creator.create();

        scheduler.startCrawling(Collections.singletonList(new HandlerDemo()), crawlCall());
        scheduler.stop();

        //Thread.sleep(30_000);
       // scheduler.stop();
    }

    private static IPageIndexer.Callback indexCall() {
        return new IPageIndexer.Callback() {

            @Override
            public void onStart() {
                System.out.println("On start / index");
            }

            @Override
            public void onStop() {
                System.out.println("On stop / index");
            }

            @Override
            public void onIndexed(@NotNull Page page) {
                System.out.println("On indexed " + page.getUrl());
            }

            @Override
            public void onUpdated(@NotNull Page page) {
                System.out.println("On updated " + page.getUrl());
            }

            @Override
            public void onLost(@NotNull Page page) {
                System.out.println("On lost " + page.getUrl());
            }
        };
    }

    private static ICrawler.Callback crawlCall() throws IOException {
        final BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Максим\\Desktop\\log.txt", false));

        return new ICrawler.Callback() {

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
                    writer.newLine();
                    writer.write("*******************************************************************************");
                    writer.newLine();
                    writer.write(page.toString());
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // System.out.println("Page accepted " + page);
            }
        };
    }

}
