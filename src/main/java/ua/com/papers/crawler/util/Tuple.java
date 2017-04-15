package ua.com.papers.crawler.util;

import lombok.Data;

/**
 * Created by Максим on 2/12/2017.
 */
@Data
public class Tuple <V1, V2> {
    V1 v1;
    V2 v2;

    public Tuple(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
}
