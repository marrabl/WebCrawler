package org.example;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JsoupHtmlFetcher implements HtmlFetcher {

    @Override
    public Document fetch(String url) throws Exception {
        Connection connection = Jsoup.connect(url);
        Document doc = connection.get();

        if (connection.response().statusCode() == 200) {
            return doc;
        } else {
            throw new Exception("Non-200 status code: " + connection.response().statusCode());
        }
    }
}
