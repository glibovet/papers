package ua.com.papers.crawler.core.main.util;

import lombok.NonNull;
import lombok.Value;
import lombok.val;
import ua.com.papers.crawler.util.Preconditions;

@Value
public class ContentType {

    String raw, type, extension;

    //application/pdf;charset=ISO-8859-1
    ContentType(@NonNull String spec) {
        val parts = spec.split("/");

        Preconditions.checkArgument(parts.length == 2, "Invalid input, was %s", spec);

        type = parts[0].trim();

        val sep = parts[1].indexOf(';');

        extension = parts[1].substring(0, sep == -1 ? parts[1].length() : sep).trim();

        this.raw = spec;
    }

}
