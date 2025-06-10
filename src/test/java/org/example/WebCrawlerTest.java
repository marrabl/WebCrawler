package org.example;

import org.example.Fetcher.HtmlFetcher;
import org.example.Writer.ReportWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

public class WebCrawlerTest {

    private HtmlFetcher fetcherMock;
    private ReportWriter reportWriterMock;
    private WebCrawler crawler;

    @BeforeEach
    public void setUp() {
        fetcherMock = mock(HtmlFetcher.class);
        reportWriterMock = mock(ReportWriter.class);

        crawler = new WebCrawler("http://test.com", 1, Set.of("test.com"), fetcherMock, reportWriterMock);
    }

    @Test
    public void testCrawl_capturesSubPagesAndWritesReport() throws Exception {
        Website root = new Website("http://test.com", 0);
        Website child = new Website("http://test.com/child", 1);
        root.setSubPages(List.of(child));

        when(fetcherMock.fetch("http://test.com", 0)).thenReturn(root);
        when(fetcherMock.fetch("http://test.com/child", 1)).thenReturn(child);

        crawler.run();

        verify(fetcherMock).fetch("http://test.com", 0);
        verify(fetcherMock).fetch("http://test.com/child", 1);
        verify(reportWriterMock).write(argThat(pages ->
                pages.stream().anyMatch(p -> p.getUrl().equals("http://test.com")) &&
                        pages.stream().anyMatch(p -> p.getUrl().equals("http://test.com/child"))
        ));
    }

    @Test
    public void testIsDomainAllowed_true() {
        assert crawler.isDomainAllowed("http://test.com/page");
    }

    @Test
    public void testIsDomainAllowed_false() {
        assert !crawler.isDomainAllowed("http://example.com/page");
    }
}
