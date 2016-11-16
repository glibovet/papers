package ua.com.papers.crawler;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Tester {
	
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		Crawler crawler = new Crawler();
		
		crawler.process(Arrays.asList(
				new URL("http://www.gla.ac.uk/services/archives/collections/business/")
				));
	}

}
