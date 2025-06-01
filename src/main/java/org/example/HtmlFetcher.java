package org.example;

import org.jsoup.nodes.Document;

public interface HtmlFetcher {
    Website fetch(String url, int depth) throws Exception;
}
