package org.example;

import org.jsoup.nodes.Document;

public interface HtmlFetcher {
    Document fetch(String url) throws Exception;
}
