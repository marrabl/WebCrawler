package org.example;

import java.util.List;

public class WebCrawler {
    private final String url;
    private final int depth;
    private final List<String> domains;

    public WebCrawler(String url, int depth, List<String> domains) {
        this.url = url;
        this.depth = depth;
        this.domains = domains;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public List<String> getDomains() {
        return domains;
    }
}
