package org.example;

import java.util.*;

public class Website {

    private final String url;
    private final int depth;
    private Map<Integer, List<String>> headingsByLevel;

    public Website(String url, int depth) {
        this.url = url;
        this.depth = depth;
        this.headingsByLevel = null;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public Map<Integer, List<String>> getHeadingsByLevel() {
        return headingsByLevel;
    }

    public void setHeadingsByLevel(Map<Integer, List<String>> headingsByLevel) {
        this.headingsByLevel = headingsByLevel;
    }
}
