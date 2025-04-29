package org.example;

import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;

public class Website {

    private final String url;
    private final int depth;
    private Document document;

    public Website(String url, int depth) {
        this.url = url;
        this.depth = depth;
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

}
