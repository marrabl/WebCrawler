package org.example;

import java.util.List;
import java.util.Map;

public class Website {
    private final String url;
    private final int depth;
    private Map<Integer, List<String>> headingsByLevel;
    private List<Website> subPages;
    private boolean isReachable = true;


    public Website(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public boolean isReachable() {
        return isReachable;
    }

    public void setReachable(boolean reachable) {
        this.isReachable = reachable;
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

    public List<Website> getSubPages() {
        return subPages != null ? subPages : List.of();
    }

    public void setSubPages(List<Website> subPages) {
        this.subPages = subPages;
    }

}
