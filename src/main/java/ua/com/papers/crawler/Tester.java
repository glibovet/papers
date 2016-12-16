package ua.com.papers.crawler;

import ua.com.papers.crawler.settings.*;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

public class Tester {

    public static void main(String[] args) throws IOException {

        AnalyzeTemplate.Builder analyze1 = new AnalyzeTemplate
                .Builder("a[href^='https://www.tutorialspoint.com/jenkins/']", 60);
                //.addAction(AnalyzeTemplate.Action.REDIRECT);

        AnalyzeTemplate.Builder analyze2 = new AnalyzeTemplate
                .Builder("body > div.main > div.container > div > div.row", 60);

        FormatTemplate.Builder format1 = new FormatTemplate
                .Builder("body > div.main > div.container > div > div.row > div.content > div > h1", 1);

        FormatTemplate.Builder format2 = new FormatTemplate
                .Builder("body > div.main > div.container > div > div.row > div.content > div > p", 2);

        PageSetting.Builder pageSettBuilder = new PageSetting.Builder()
                .addAnalyzeTemplate(analyze1.build())
                .addAnalyzeTemplate(analyze2.build())
                .addFormatTemplate(format1.build())
                .addFormatTemplate(format2.build());

        Settings.Builder settings = new Settings.Builder(
                new SchedulerSetting(),
                Collections.singletonList(new URL("http://www.tutorialspoint.com/jenkins/")),
                Collections.singletonList(pageSettBuilder.build())
        );

    }

}
