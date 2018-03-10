package ua.com.papers.crawler.settings;

import lombok.Value;

@Value
public class AnalyzeWeight implements Comparable<AnalyzeWeight> {

    public static final int MIN_WEIGHT = 0;
    public static final int MAX_WEIGHT = 100;
    public static final int DEFAULT_WEIGHT = 70;

    int weight;

    public static AnalyzeWeight ofValue(int weight) {
        return new AnalyzeWeight(weight);
    }

    private AnalyzeWeight(int weight) {
        if (weight < MIN_WEIGHT || weight > MAX_WEIGHT)
            throw new IllegalArgumentException(
                    String.format("weight < %d || weight > %d, was %s", MIN_WEIGHT, MAX_WEIGHT, weight));

        this.weight = weight;
    }

    @Override
    public int compareTo(AnalyzeWeight o) {
        return Integer.compare(weight, o.weight);
    }
}
