package org.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class WebCrawler {
    private final String url;
    private final int depth;
    private final List<String> domains;

    private final Set<String> visitedLinks = new HashSet<>();   // set because no duplicates allowed
    private final List<String> outputLines = new ArrayList<>();

    public WebCrawler(String url, int depth, List<String> domains) {
        this.url = url;
        this.depth = depth;
        this.domains = domains;
    }

    // recursive methode
    public void run() {
        crawl(0, url);
    }

    private void crawl(int currentDepth, String url) {
        if (currentDepth > depth || visitedLinks.contains(url) || !isDomainAllowed(url)) {
            return;
        }

        visitedLinks.add(url);

        Document doc = request(url);
        if (doc != null) {
            for (Element link : doc.select("a[href]")) {

                String nextLink = link.absUrl("href");
                crawl(currentDepth + 1, nextLink);
            }
        }
    }


    public Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();

            if (con.response().statusCode() == 200) {
                return doc;
            }

        } catch (IOException e) {
            System.err.println("Error requesting " + url + ": " + e.getMessage());
        }
        return null;
    }

    public boolean isDomainAllowed(String url) {
        for (String domain : domains) {
            if (url.contains(domain)) {
                return true;
            }
        }
        return false;
    }
}
