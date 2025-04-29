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
    private final int maxDepth;
    private final List<String> domains;

    private final Set<String> visitedLinks = new HashSet<>();

    public WebCrawler(String url, int maxDepth, List<String> domains) {
        this.url = url;
        this.maxDepth = maxDepth;
        this.domains = domains;
    }

    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("report.md"))) {
            crawl(0, url, writer);
            System.out.println("Crawling complete.");

        } catch (IOException e) {
            System.err.println("Error writing report: " + e.getMessage());
        }
    }

    protected void crawl(int currentDepth, String url, BufferedWriter writer) throws IOException {
        if (currentDepth > maxDepth || visitedLinks.contains(url) || !isDomainAllowed(url)) {
            return;
        }

        visitedLinks.add(url);
        Document doc = fetchDocument(url);

        writer.write(formatOutput(url, doc, currentDepth,doc != null ));
        writer.newLine();
        writer.flush();

        if (doc != null) {
            for (Element link : doc.select("a[href]")) {
                String nextLink = link.absUrl("href");
                crawl(currentDepth + 1, nextLink, writer);
            }
        }
    }

    public Document fetchDocument(String url) {
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

    private String formatOutput(String url, Document doc, int depth, boolean isSuccessful) {
        StringBuilder output = new StringBuilder();

        String indent = "-->".repeat(depth);

        if (isSuccessful) {
            output.append("<br>").append(indent).append(" link to <a>").append(url).append("</a>");
            if (doc != null) {
                output.append("\n<br>depth: ").append(depth);
                output.append("\n").append(extractHeadings(doc, depth));
            }
        } else {
            output.append("<br>").append(indent).append(" broken link <a>").append(url).append("</a>");
        }

        return output.toString();
    }

    private String extractHeadings(Document doc, int depth) {
        StringBuilder headingsOutput = new StringBuilder();
        String indent = "# ".repeat(depth);

        for (int level = 1; level <= 3; level++) {
            for (Element heading : doc.select("h" + level)) {
                String prefix = "#".repeat(level);
                String adjustedPrefix = indent + prefix;
                headingsOutput.append(adjustedPrefix).append(" ").append(heading.text()).append("\n");
            }
        }

        return headingsOutput.toString().trim();
    }

}
