package ua.com.papers.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {

	static final Pattern pattern = Pattern
			.compile("\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov"
					+ "|mil|biz|info|mobi|name|aero|jobs|museum" + "|travel|[a-z]{2}))(:[\\d]{1,5})?"
					+ "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
					+ "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
					+ "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

	public void process(Collection<URL> src) throws IOException {

		Queue<URL> urls = new LinkedList<>(src);
		Map<URL, Collection<Page>> crawledPages = new HashMap<>();

		BufferedWriter writer = new BufferedWriter(new FileWriter("logs.txt"));

		while (!urls.isEmpty() && urls.size() <= 100) {

			final URL url = urls.poll();
			final Page page = crawlPage(url);

			Collection<Page> crawledPagesColl = crawledPages.get(url);

			if (crawledPagesColl == null) {
				crawledPagesColl = new ArrayList<>(1);
			}
			crawledPagesColl.add(page);
			crawledPages.put(url, crawledPagesColl);

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

	private Page crawlPage(URL url) {

		Page page = new Page();
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

	private Set<URL> extractUrls(Page page) {

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

}
