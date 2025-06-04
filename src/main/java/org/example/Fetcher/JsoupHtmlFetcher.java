package org.example.Fetcher;

import org.example.Website;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

public class JsoupHtmlFetcher implements HtmlFetcher {

    @Override
    public Website fetch(String url, int depth) throws Exception {
        Document doc = Jsoup.connect(url).get();

        Website site = new Website(url, depth);
        site.setHeadingsByLevel(extractHeadings(doc));
        site.setSubPages(extractLinks(doc, depth));

        return site;
    }

    private Map<Integer, List<String>> extractHeadings(Document doc) {
        Map<Integer, List<String>> headings = new HashMap<>();
        for (int i = 1; i <= 6; i++) {
            List<String> list = new ArrayList<>();
            for (Element el : doc.select("h" + i)) {
                list.add(el.text());
            }
            headings.put(i, list);
        }
        return headings;
    }

    private List<Website> extractLinks(Document doc, int currentDepth) {
        List<Website> links = new ArrayList<>();
        for (Element link : doc.select("a[href]")) {
            String absUrl = link.absUrl("href");
            if (!absUrl.isEmpty()) {
                links.add(new Website(absUrl, currentDepth + 1));
            }
        }
        return links;
    }
}
