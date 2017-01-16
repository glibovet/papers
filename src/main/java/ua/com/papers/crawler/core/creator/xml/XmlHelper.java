package ua.com.papers.crawler.core.creator.xml;

import org.w3c.dom.Element;

/**
 * Created by Максим on 1/16/2017.
 */
public final class XmlHelper {

    private XmlHelper() {
        throw new RuntimeException("shouldn't be instantiated");
    }

    public static long parseLong(Element e) {
        return e == null ? 0L : Long.valueOf(e.getTextContent());
    }

    public static int parseInt(Element e) {
        return e == null ? 0 : Integer.valueOf(e.getTextContent());
    }

    public static long parseLong(Element e, String attr) {
        return e == null ? 0L : Long.valueOf(e.getAttribute(attr));
    }

    public static int parseInt(Element e, String attr) {
        return e == null ? 0 : Integer.valueOf(e.getAttribute(attr));
    }

}
