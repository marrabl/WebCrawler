package org.example;

public interface HtmlFetcher {
    Website fetch(String url, int depth) throws Exception;
}
