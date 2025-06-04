package org.example;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebCrawlerTest {
    /*private WebCrawler webCrawler;
    private StringWriter stringWriter;
    private BufferedWriter writer;

    @BeforeEach
    public void setUp() {
        stringWriter = new StringWriter();
        writer = new BufferedWriter(stringWriter);

        //anonymous subclass to override fetchDocument for unit testing
        webCrawler = new WebCrawler("http://test.com", 1, List.of("test.com")) {
            @Override
            protected Document fetchDocument(String url) {
                Document doc = mock(Document.class);

                //mock a single link
                Element link = mock(Element.class);
                when(link.absUrl("href")).thenReturn("http://test.com/child");
                when(doc.select("a[href]")).thenReturn(new Elements(link));

                //mock headings
                Element heading = mock(Element.class);
                when(heading.text()).thenReturn("Test Heading");
                when(doc.select("h1")).thenReturn(new Elements(heading));

                return doc;
            }
        };
    }

    @Test
    public void testCrawl_generatesExpectedOutput() throws Exception {
        Website root = new Website("http://test.com", 0);

        WebCrawler testCrawler = new WebCrawler("http://test.com", 1, List.of("test.com")) {
            @Override
            protected Document fetchDocument(String url) {
                Document doc = mock(Document.class);

                //mock a link for recursion
                Element link = mock(Element.class);
                when(link.absUrl("href")).thenReturn("http://test.com/child");
                when(doc.select("a[href]")).thenReturn(new Elements(link));

                //mock headings for all levels (avoid null return)
                for (int i = 1; i <= 6; i++) {
                    when(doc.select("h" + i)).thenReturn(new Elements());
                }

                //add one actual heading
                Element h1 = mock(Element.class);
                when(h1.text()).thenReturn("Test Heading");
                when(doc.select("h1")).thenReturn(new Elements(h1));

                return doc;
            }
        };

        //use reflection to call private crawl method
        var crawlMethod = WebCrawler.class.getDeclaredMethod("crawl", Website.class, BufferedWriter.class);
        crawlMethod.setAccessible(true);
        crawlMethod.invoke(testCrawler, root, writer);
        writer.flush();

        String result = stringWriter.toString();
        assertTrue(result.contains("link to"));
        assertTrue(result.contains("http://test.com"));
        assertTrue(result.contains("Test Heading"));
    }

    @Test
    public void testIsDomainAllowed_true() {
        assertTrue(webCrawler.isDomainAllowed("https://test.com/page"));
    }

    @Test
    public void testIsDomainAllowed_false() {
        assertFalse(webCrawler.isDomainAllowed("https://otherdomain.com/page"));
    }

    @Test
    public void testFormatOutput_validPage() throws Exception {
        Website page = new Website("http://test.com", 1);
        Document doc = mock(Document.class);

        //return empty elements for all heading levels to avoid NPE
        for (int i = 1; i <= 6; i++) {
            when(doc.select("h" + i)).thenReturn(new Elements());
        }

        var method = WebCrawler.class.getDeclaredMethod("formatOutput", Website.class, Document.class, boolean.class);
        method.setAccessible(true);
        String output = (String) method.invoke(webCrawler, page, doc, true);

        assertTrue(output.contains("link to"));
        assertTrue(output.contains("<a>http://test.com</a>"));
    }

    @Test
    public void testFormatOutput_brokenPage() throws Exception {
        Website page = new Website("http://test.com/broken", 1);

        var method = WebCrawler.class.getDeclaredMethod("formatOutput", Website.class, Document.class, boolean.class);
        method.setAccessible(true);

        String output = (String) method.invoke(webCrawler, page, null, false);
        assertTrue(output.contains("broken link"));
    }

    @Test
    public void testFormatHeadings_outputsCorrectFormat() throws Exception {
        Document doc = mock(Document.class);

        Element h1 = mock(Element.class);
        when(h1.text()).thenReturn("Heading1");

        Element h2 = mock(Element.class);
        when(h2.text()).thenReturn("Heading2");

        when(doc.select("h1")).thenReturn(new Elements(h1));
        when(doc.select("h2")).thenReturn(new Elements(h2));
        for (int i = 3; i <= 6; i++) {
            when(doc.select("h" + i)).thenReturn(new Elements());
        }

        var method = WebCrawler.class.getDeclaredMethod("formatHeadings", Document.class, int.class);
        method.setAccessible(true);

        String output = (String) method.invoke(webCrawler, doc, 1);
        assertTrue(output.contains("# # Heading1"));
        assertTrue(output.contains("# ## Heading2"));
    }*/
}
