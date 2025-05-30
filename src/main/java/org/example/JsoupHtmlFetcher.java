package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

public class JsoupHtmlFetcher implements HtmlFetcher {

    @Override
    public Document fetch(String url) throws Exception {
        Document doc = Jsoup.connect(url).get();
        if (doc.baseUri().isEmpty()) {
            throw new Exception("Unable to fetch document from: " + url);
        }
        return doc;
    }

    public Map<Integer, List<String>> extractHeadings(Document doc) {
        Map<Integer, List<String>> headings = new HashMap<>();
        for (int level = 1; level <= 6; level++) {
            List<String> texts = new ArrayList<>();
            for (Element el : doc.select("h" + level)) {
                texts.add(el.text());
            }
            if (!texts.isEmpty()) {
                headings.put(level, texts);
            }
        }
        return headings;
    }
}
