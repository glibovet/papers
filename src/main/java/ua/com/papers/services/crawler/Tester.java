package ua.com.papers.services.crawler;

import lombok.val;
import org.apache.commons.io.FilenameUtils;

import java.util.regex.Pattern;

public class Tester {

    public static void main(String[] args) throws Exception {

        System.out.println(FilenameUtils.getExtension("http://journals.uran.ua/index.php/1991-0177/article/view/118700/pdf_245"));

        val str = "(Oleksandr Aghyppo) Ажиппо Олександр Юрійович,\t(Tatyana Dorofeeva) Дорофєєва Тетяна Іванівна,\t(Yaroslavna Puhach) Пугач Ярославна Ігорівна,\t(Galina Artem’yeva) Артем’єва Галина Павлівна,\t(Mariia Nechytailo) Нечитайло Марія Валеріївна,\t(Valeriy Druz) Друзь Валерій Анатолійович";


        val p = Pattern.compile("[(\\[].*?[)\\]]");

        System.out.println("[Oleksandr Aghyppo] Ажиппо Олександр Юрійович,\t[Tatyana Dorofeeva] Дорофєєва Тетяна Іванівна".replaceAll(p.pattern(), ""));


        System.out.println("xfooxxxxxxfoo".replaceAll(".*foo", "1"));

       // System.out.println(Arrays.toString("Боков, В.А.".trim().split(", ")));

     //   System.out.println(new File("/home/max/IdeaProjects/papers/src/main/resources/crawler/crawler-settings.xml").exists());

       // val doc = Jsoup.parse("<a href=\"/handle/123456789/6537\"> <span class=\"Z3988\">Кримський науковий центр</span> </a>", "");

       // System.out.println(doc.select("a"));
       // System.out.println(doc.select("a").get(0).absUrl("abs:href"));

        /*val doc1 = Jsoup.parse("<a href=\"/bitstream/handle/123456789/2011/06-Gershenzon.pdf?sequence=1\">Перегляд/<wbr xmlns:i18n=\"http://apache.org/cocoon/i18n/2.1\">Відкрити</a>", "http://dspace.nbuv.gov.ua/");

        System.out.println(doc1.select("a").get(0).absUrl("abs:href"));

        URL u = new URL("https://www.google.com.ua/search?q=java+url+example&oq=java+url+exa&aqs=chrome.1.69i57j0l5.6611j0j7&sourceid=chrome&ie=UTF-8");

        System.out.println(u.getProtocol() + "://" + u.getHost());*/

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

}
