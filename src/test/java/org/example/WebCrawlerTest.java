package org.example;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WebCrawlerTest {
    private WebCrawler webCrawler;

    @BeforeEach
    public void setUp() {
        webCrawler = new WebCrawler("https://example.com", 1, List.of("example.com"));
    }

    /*
    @Test
    public void testCrawl() {}
     */

    @Test
    public void testIsDomainAllowed_true() {
        assertTrue(webCrawler.isDomainAllowed("https://example.com/page"));
    }

    @Test
    public void testIsDomainAllowed_false() {
        assertFalse(webCrawler.isDomainAllowed("https://notallowed.com/page"));
    }

    @Test
    public void testFetchDocument_success() {
        Document document = webCrawler.fetchDocument("https://example.com");
        assertNotNull(document);
        assertTrue(document.title().contains("Example"));
    }

    @Test
    public void testFetchDocument_InvalidURL_ShouldReturnNull() {
        WebCrawler crawler = new WebCrawler("https://invalid.url", 1, Collections.singletonList("invalid.url"));
        Document document = crawler.fetchDocument("https://invalid.url/thispagedoesnotexist");
        assertNull(document);
    }
}
