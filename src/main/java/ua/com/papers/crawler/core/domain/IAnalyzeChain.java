package ua.com.papers.crawler.core.domain;


/**
 * Created by Максим on 12/1/2016.
 */
public interface IAnalyzeChain {

    int getWeight();

    boolean satisfies(Page p);

}
