package ua.com.papers.services.crawler;

import lombok.val;
import org.jsoup.Jsoup;
import ua.com.papers.crawler.core.domain.ICrawler;
import ua.com.papers.crawler.core.domain.IPageIndexer;
import ua.com.papers.crawler.core.domain.bo.Page;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;

public class Tester {

    public static void main(String[] args) throws Exception {

        val doc = Jsoup.parse("<a href=\"/handle/123456789/6537\"> <span class=\"Z3988\">Кримський науковий центр</span> </a>", "");

        System.out.println(doc.select("a"));
        System.out.println(doc.select("a").get(0).absUrl("abs:href"));

      /*val creator = XmlCrawlerManagerFactory.newInstance("src/main/resources/crawler/crawler-settings.xml");
        val scheduler = creator.create();

        scheduler.startCrawling(
                Collections.singletonList(new HandlerDemo("C:\\Users\\Максим\\Desktop\\mlog.txt")),
                crawlCall()
        );
        scheduler.stop();*/

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

            @Override
            public void onIndexException(@NotNull URL url, @NotNull Throwable th) {

            }
        };
    }

    private static ICrawler.Callback crawlCall() throws IOException {

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
                System.out.println("On page rejected " + page.getUrl());
            }

            @Override
            public void onStop() {
                System.out.println("On stop");
            }

            @Override
            public void onCrawlException(@NotNull URL url, @NotNull Throwable th) {
                System.out.println("On exception " + th);
            }

            @Override
            public void onPageAccepted(@NotNull Page page) {
                System.out.println("Page accepted " + page.getUrl());
            }
        };
    }

}
