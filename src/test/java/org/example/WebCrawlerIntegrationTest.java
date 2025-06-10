package org.example;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.example.Fetcher.JsoupHtmlFetcher;
import org.example.Writer.ReportWriter;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

public class WebCrawlerIntegrationTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void startServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8089));
        wireMockServer.start();

        configureFor("localhost", 8089);

        stubFor(get(urlEqualTo("/"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withBody("<html><body><h1>Home</h1><a href='/about'>About</a></body></html>")));

        stubFor(get(urlEqualTo("/about"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withBody("<html><body><h2>About Us</h2></body></html>")));
    }

    @AfterAll
    public static void stopServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    public void testCrawlingWithRealFetcher() throws IOException {
        String rootUrl = "http://localhost:8089/";
        JsoupHtmlFetcher realFetcher = new JsoupHtmlFetcher();

        TestReportWriter reportWriter = new TestReportWriter();

        WebCrawler crawler = new WebCrawler(rootUrl, 1, Set.of("localhost"), realFetcher, reportWriter);
        crawler.run();

        List<Website> results = reportWriter.getWrittenPages();

        assertEquals(2, results.size());

        assertTrue(results.stream().anyMatch(w -> w.getUrl().equals("http://localhost:8089/")));
        assertTrue(results.stream().anyMatch(w -> w.getUrl().equals("http://localhost:8089/about")));

        Website home = results.stream().filter(w -> w.getUrl().endsWith("/")).findFirst().orElseThrow();
        assertEquals("Home", home.getHeadingsByLevel().get(1).get(0));

        Website about = results.stream().filter(w -> w.getUrl().contains("about")).findFirst().orElseThrow();
        assertEquals("About Us", about.getHeadingsByLevel().get(2).get(0));
    }

    static class TestReportWriter extends ReportWriter {
        private List<Website> writtenPages;

        public TestReportWriter() throws IOException {
            super();
        }

        @Override
        public void write(List<Website> websites) {
            this.writtenPages = websites;
        }

        public List<Website> getWrittenPages() {
            return writtenPages;
        }
    }
}
