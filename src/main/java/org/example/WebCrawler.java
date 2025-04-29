package org.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebCrawler {

    private static final Logger logger = Logger.getLogger(WebCrawler.class.getName());

    private final String startUrl;
    private final int maxDepth;
    private final List<String> allowedDomains;
    private final Set<String> visitedLinks = new HashSet<>();

    public WebCrawler(String startUrl, int maxDepth, List<String> allowedDomains) {
        this.startUrl = startUrl;
        this.maxDepth = maxDepth;
        this.allowedDomains = allowedDomains;
    }

    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("report.md"))) {
            Website startWebsite = new Website(startUrl, 0);

            crawl(startWebsite, writer);
            logger.info("Crawling complete.");

        } catch (IOException e) {
            handleException("Error writing report", e);
        }
    }

    private void crawl(Website website, BufferedWriter writer) throws IOException {
        if (!shouldVisit(website)) return;

        visitedLinks.add(website.getUrl());
        processPage(website, writer);
    }

    private boolean shouldVisit(Website website) {
        return website.getDepth() <= maxDepth && !visitedLinks.contains(website.getUrl()) && isDomainAllowed(website.getUrl());
    }

    private void processPage(Website website, BufferedWriter writer) throws IOException {
        Document document = fetchDocument(website.getUrl());
        boolean isPageAccessible = document != null;

        website.setDocument(document);
        writeLine(writer, formatOutput(website, document, isPageAccessible));

        if (isPageAccessible) {
            for (Element link : document.select("a[href]")) {
                String nextUrl = link.absUrl("href");
                Website nextWebsite = new Website(nextUrl, website.getDepth() + 1);
                crawl(nextWebsite, writer);
            }
        }
    }

    protected Document fetchDocument(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            if (connection.response().statusCode() == 200) {
                return document;
            } else {
                logger.warning("No 200 response for URL: " + url);
            }
        } catch (IOException e) {
            handleException("Error requesting URL: " + url, e);
        }
        return null;
    }

    protected boolean isDomainAllowed(String url) {
        return allowedDomains.stream().anyMatch(url::contains);
    }

    private String formatOutput(Website website, Document document, boolean isSuccessful) {
        StringBuilder output = new StringBuilder();
        String depthIndicator = "-->".repeat(website.getDepth());

        if (isSuccessful) {
            output.append("<br>").append(depthIndicator).append(" link to <a>").append(website.getUrl()).append("</a>");
            output.append("\n<br>depth: ").append(website.getDepth());
            output.append("\n").append(formatHeadings(document, website.getDepth()));

        } else {
            output.append("<br>").append(depthIndicator).append(" broken link <a>").append(website.getUrl()).append("</a>");
        }

        return output.toString();
    }

    private String formatHeadings(Document document, int depth) {
        StringBuilder formattedHeadings = new StringBuilder();
        String baseIndent = "# ".repeat(depth);

        for (int level = 1; level <= 6; level++) {
            for (Element heading : document.select("h" + level)) {
                String headingPrefix = "#".repeat(level);
                formattedHeadings
                        .append(baseIndent)
                        .append(headingPrefix)
                        .append(" ")
                        .append(heading.text())
                        .append("\n");
            }
        }

        return formattedHeadings.toString().trim();
    }

    private void writeLine(BufferedWriter writer, String content) throws IOException {
        writer.write(content);
        writer.newLine();
        writer.flush();
    }

    private void handleException(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
