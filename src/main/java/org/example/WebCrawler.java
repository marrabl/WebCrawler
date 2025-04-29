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

    private static final Logger LOGGER = Logger.getLogger(WebCrawler.class.getName());

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
            crawl(0, startUrl, writer);
            LOGGER.info("Crawling complete.");
        } catch (IOException e) {
            handleException("Error writing report", e);
        }
    }

    protected void crawl(int currentDepth, String url, BufferedWriter writer) throws IOException {
        if (!shouldVisit(url, currentDepth)) return;

        visitedLinks.add(url);
        processPage(url, currentDepth, writer);
    }

    private boolean shouldVisit(String url, int depth) {
        return depth <= maxDepth && !visitedLinks.contains(url) && isDomainAllowed(url);
    }

    private void processPage(String url, int depth, BufferedWriter writer) throws IOException {
        Document document = fetchDocument(url);
        boolean isSuccessful = document != null;

        writeLine(writer, formatOutput(url, document, depth, isSuccessful));

        if (isSuccessful) {
            for (Element link : document.select("a[href]")) {
                String nextUrl = link.absUrl("href");
                crawl(depth + 1, nextUrl, writer);
            }
        }
    }

    public Document fetchDocument(String url) {
        try {
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            if (connection.response().statusCode() == 200) {
                return document;
            } else {
                LOGGER.warning("Non-200 response for URL: " + url);
            }
        } catch (IOException e) {
            handleException("Error requesting URL: " + url, e);
        }
        return null;
    }

    public boolean isDomainAllowed(String url) {
        return allowedDomains.stream().anyMatch(url::contains);
    }

    private String formatOutput(String url, Document document, int depth, boolean isSuccessful) {
        StringBuilder output = new StringBuilder();
        String depthIndicator = "-->".repeat(depth);

        if (isSuccessful) {
            output.append("<br>").append(depthIndicator).append(" link to <a>").append(url).append("</a>");
            output.append("\n<br>depth: ").append(depth);
            output.append("\n").append(formatHeadings(document, depth));
        } else {
            output.append("<br>").append(depthIndicator).append(" broken link <a>").append(url).append("</a>");
        }

        return output.toString();
    }

    private String formatHeadings(Document document, int depth) {
        StringBuilder formattedHeadings = new StringBuilder();
        String baseIndent = "# ".repeat(depth);

        for (int level = 1; level <= 3; level++) {
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
}
