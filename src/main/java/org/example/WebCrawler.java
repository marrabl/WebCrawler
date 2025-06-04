package org.example;

import org.example.Fetcher.HtmlFetcher;
import org.example.Fetcher.JsoupHtmlFetcher;
import org.example.Writer.ReportWriter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebCrawler {

    private static final Logger logger = Logger.getLogger(WebCrawler.class.getName());

    private final String startUrl;
    private final int maxDepth;
    private final Set<String> allowedDomains;

    // ConcurrentHashMap for concurrent threads without synchronized blocks
    private final Set<String> visitedLinks = ConcurrentHashMap.newKeySet();
    private final Set<Website> crawledPages = ConcurrentHashMap.newKeySet();

    private final ReportWriter reportWriter;
    private final HtmlFetcher fetcher;

    private final ExecutorService executor = Executors.newFixedThreadPool(50);
    private final AtomicInteger activeTasks = new AtomicInteger(0); // thread-safe counter for active tasks

    public WebCrawler(String startUrl, int maxDepth, Set<String> allowedDomains) {
        this.startUrl = startUrl;
        this.maxDepth = maxDepth;
        this.allowedDomains = allowedDomains;
        this.fetcher = new JsoupHtmlFetcher();

        try {
            this.reportWriter = new ReportWriter();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize ReportWriter", e);
        }
    }

    // starts the crawling process, writing results to a report file
    public void run() {
        Website rootPage = new Website(startUrl, 0);

        activeTasks.incrementAndGet(); // increment activeTasks and return value
        executor.submit(() -> {
            try {
                crawl(rootPage);
            } finally {
                taskDone();
            }
        });

        try {
            waitForCompletion();
            reportWriter.write(new ArrayList<>(crawledPages));
            logger.info("[" + Thread.currentThread().getName() + "] Crawling complete.");

        } catch (InterruptedException e) {
            handleException("[" + Thread.currentThread().getName() + "] Interrupted during crawling", e);
            Thread.currentThread().interrupt();
        }
    }

    private void crawl(Website page) {
        if (!isEligibleForVisit(page)) return;

        if (!visitedLinks.add(page.getUrl())) return;

        logger.info("[" + Thread.currentThread().getName() + "] Crawling URL: " + page.getUrl() + " at depth " + page.getDepth());

        Website fetchedPage = fetchWebsite(page.getUrl(), page.getDepth());
        if (fetchedPage == null) return;

        crawledPages.add(fetchedPage);

        for (Website subPage : fetchedPage.getSubPages()) {
            activeTasks.incrementAndGet();
            executor.submit(() -> {
                try {
                    crawl(subPage);
                } finally {
                    taskDone();
                }
            });
        }
    }

    private Website fetchWebsite(String url, int depth) {
        try {
            return fetcher.fetch(url, depth);

        } catch (Exception e) {
            handleException("[" + Thread.currentThread().getName() + "] Error fetching website: " + url, e);
            return null;
        }
    }

    private void waitForCompletion() throws InterruptedException {
        boolean terminated = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        if (!terminated) {
            logger.warning("Executor did not terminate properly.");
        }
    }

    private void taskDone() {
        if (activeTasks.decrementAndGet() == 0) {
            executor.shutdown();
        }
    }

    // checks if the URL is within the allowed domains
    protected boolean isDomainAllowed(String url) {
        return allowedDomains.stream().anyMatch(url::contains);
    }

    // checks if maxDepth is reached, a link was visited already, or URL is in the allowed domain
    private boolean isEligibleForVisit(Website page) {
        return page.getDepth() <= maxDepth && isDomainAllowed(page.getUrl());
    }

    private void handleException(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
}
