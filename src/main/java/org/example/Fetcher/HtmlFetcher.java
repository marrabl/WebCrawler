package org.example.Fetcher;

import org.example.Website;

public interface HtmlFetcher {
    Website fetch(String url, int depth) throws Exception;
}
