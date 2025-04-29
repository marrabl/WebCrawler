package org.example;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebCrawlerTest {
    private WebCrawler webCrawler;

    @BeforeEach
    public void setUp() {
        webCrawler = spy(new WebCrawler("https://example.com", 2, List.of("example.com")));
    }

    /*@Test
    public void testCrawlWritesExpectedOutput() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Document mockDoc = new Document("https://example.com");
        Element h1 = new Element(Tag.valueOf("h1"), "").appendChild(new TextNode("Hello World"));
        mockDoc.body().appendChild(h1);

        // Mock die fetchDocument-Methode
        doReturn(mockDoc).when(webCrawler).fetchDocument("https://example.com");

        StringWriter stringWriter = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);

        // Statt run() direkt crawl() aufrufen
        webCrawler.getClass().getDeclaredMethod("crawl", int.class, String.class, BufferedWriter.class)
                .setAccessible(true); // nur falls private
        webCrawler.getClass().getDeclaredMethod("crawl", int.class, String.class, BufferedWriter.class)
                .invoke(webCrawler, 0, "https://example.com", bufferedWriter);

        bufferedWriter.flush();
        String output = stringWriter.toString();

        assertTrue(output.contains("Hello World"), "Heading should be included in output");
        assertTrue(output.contains("link to"), "Should contain link output");
    }*/


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
