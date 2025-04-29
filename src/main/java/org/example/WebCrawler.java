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
            Website rootPage = new Website(startUrl, 0);
            crawl(rootPage, writer);
            logger.info("Crawling complete.");

        } catch (IOException e) {
            handleException("Error writing report", e);
        }
    }

    private void crawl(Website page, BufferedWriter writer) throws IOException {
        if (!isEligibleForVisit(page)) return;

        visitedLinks.add(page.getUrl());
        fetchAndSetDocument(page);
        writeReportEntry(page, writer);
        crawlSubPages(page, writer);
    }

    private void fetchAndSetDocument(Website page) {
        Document doc = fetchDocument(page.getUrl());
        page.setDocument(doc);
    }

    private void writeReportEntry(Website page, BufferedWriter writer) throws IOException {
        boolean pageAccessible = page.getDocument() != null;
        String output = formatOutput(page, page.getDocument(), pageAccessible);
        writeLine(writer, output);
    }

    private void crawlSubPages(Website page, BufferedWriter writer) throws IOException {
        Document doc = page.getDocument();
        if (doc == null) return;

        for (Element link : doc.select("a[href]")) {
            String nextUrl = link.absUrl("href");
            Website subPage = new Website(nextUrl, page.getDepth() + 1);
            crawl(subPage, writer);
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

    private boolean isEligibleForVisit(Website page) {
        return page.getDepth() <= maxDepth &&
                !visitedLinks.contains(page.getUrl()) &&
                isDomainAllowed(page.getUrl());
    }

    private String formatOutput(Website page, Document doc, boolean isPageValid) {
        StringBuilder output = new StringBuilder();
        String depthArrow = "-->".repeat(page.getDepth());

        if (isPageValid) {
            output.append("<br>").append(depthArrow).append(" link to <a>")
                    .append(page.getUrl()).append("</a>");
            output.append("\n<br>depth: ").append(page.getDepth());
            output.append("\n").append(formatHeadings(doc, page.getDepth()));
        } else {
            output.append("<br>").append(depthArrow).append(" broken link <a>")
                    .append(page.getUrl()).append("</a>");
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
                        .append("\n")
                        .append(baseIndent)
                        .append(headingPrefix)
                        .append(" ")
                        .append(heading.text());
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
