package ua.com.papers.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimplestCrawler {

	static final Pattern pattern = Pattern
			.compile("\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov"
					+ "|mil|biz|info|mobi|name|aero|jobs|museum" + "|travel|[a-z]{2}))(:[\\d]{1,5})?"
					+ "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
					+ "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
					+ "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

	public void process(Collection<URL> src) throws IOException {

		Queue<URL> urls = new LinkedList<>(src);
		Map<URL, Collection<mPage>> crawledPages = new HashMap<>();

		BufferedWriter writer = new BufferedWriter(new FileWriter("logs.txt"));

		while (!urls.isEmpty() && urls.size() <= 100) {

			final URL url = urls.poll();
			final mPage page = crawlPage(url);

			Collection<mPage> crawledPagesColl = crawledPages.get(url);

			if (crawledPagesColl == null) {
				crawledPagesColl = new ArrayList<>(1);
			}
			crawledPagesColl.add(page);
			crawledPages.put(url, crawledPagesColl);

			findHrefs(page.content);
			//System.out.println(extractContent(page.content));
			Set<URL> extracted = extractUrls(page);

			for (final URL u : extracted) {
				if (!urls.contains(u) && !crawledPages.containsKey(u)) {
					urls.add(u);
				}

				reorder(urls);
			}

			writer.write(urls.toString());
			writer.write("\n");
			writer.flush();
			System.out.println("mSize " + urls.size());
		}
		writer.close();
		System.out.println(urls);
	}

	private void reorder(Queue<URL> queue) {

	}

	private mPage crawlPage(URL url) {

		mPage page = new mPage();
		page.url = url.toString();

		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				page.content += inputLine;
			//	 System.out.println(inputLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return page;
	}

	private Set<URL> extractUrls(mPage page) {

		final Set<URL> urls = new HashSet<>();

		
			Matcher matcher = pattern.matcher(page.content);

			while (matcher.find()) {
				try {

					final String group = matcher.group();

					urls.add(new URL(group));

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
		}

		return urls;
	}

	private void findHrefs(String html) {
		Document doc = Jsoup.parse(html);

		Elements links = doc.select("a[href^='/article/c/']");

		for(Element element : links) {
			System.out.println(element.attr("href"));
			System.out.println(element.ownText());
		}
	}

	private String extractContent(String html) {

		Document doc = Jsoup.parse(html);
		Elements content = doc.select("#content_54961");

		return content.text();
	}

}
