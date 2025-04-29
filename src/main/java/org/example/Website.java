package org.example;

import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;

public class Website {

    private final String url;
    private final int depth;
    private Document document;
    private final Set<String> visitedLinks;

    public Website(String url, int depth) {
        this.url = url;
        this.depth = depth;
        this.visitedLinks = new HashSet<>();
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public boolean hasVisited(String link) {
        return visitedLinks.contains(link);
    }

    public void addVisitedLink(String link) {
        visitedLinks.add(link);
    }

    @Override
    public String toString() {
        return "Website{" +
                "url='" + url + '\'' +
                ", depth=" + depth +
                '}';
    }
}
