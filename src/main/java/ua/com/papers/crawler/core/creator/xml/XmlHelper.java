package ua.com.papers.crawler.core.creator.xml;

import lombok.val;
import org.w3c.dom.Element;

/**
 * <p>
 *     Helper class which provides utility methods
 *     to simplify XML parsing process
 * </p>
 * Created by Максим on 1/16/2017.
 */
public final class XmlHelper {

    private XmlHelper() {
        throw new RuntimeException("shouldn't be instantiated");
    }

    public static long parseLong(Element e, long def) {
        if (e == null) return def;
        val content = e.getTextContent();
        return isNullOrEmpty(content) ? def : Long.valueOf(content);
    }

    public static long parseLong(Element e) {
        return parseLong(e, 0L);
    }

    public static int parseInt(Element e, int def) {
        if (e == null) return def;
        val content = e.getTextContent();
        return isNullOrEmpty(content) ? def : Integer.valueOf(content);
    }

    public static int parseInt(Element e) {
        return parseInt(e, 0);
    }

    public static long parseLong(Element e, String attr, long def) {
        if (e == null) return def;
        val attrVal = e.getAttribute(attr);
        return isNullOrEmpty(attrVal) ? def : Long.valueOf(attrVal);
    }

    public static long parseLong(Element e, String attr) {
        return parseLong(e, attr, 0L);
    }

    public static int parseInt(Element e, String attr, int def) {
        if (e == null) return def;
        val attrVal = e.getAttribute(attr);
        return isNullOrEmpty(attrVal) ? def : Integer.valueOf(attrVal);
    }

    public static int parseInt(Element e, String attr) {
        return parseInt(e, attr, 0);
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
