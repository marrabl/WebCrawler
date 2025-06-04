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

    // starts the crawling process
    public void run() {
        initializeCrawling();

        try {
            writeReport();

        } catch (InterruptedException e) {
            handleCrawlingInterruption(e);
        }
    }

    private void initializeCrawling() {
        Website rootPage = new Website(startUrl, 0);
        submitCrawlingTask(rootPage);
    }

    // writes the report after all tasks are finished
    private void writeReport() throws InterruptedException {
        waitForCompletion();
        reportWriter.write(new ArrayList<>(crawledPages));
        logger.info("[" + Thread.currentThread().getName() + "] Crawling complete.");
    }

    private void handleCrawlingInterruption(Exception e) {
        handleException("[" + Thread.currentThread().getName() + "] Interrupted during crawling", e);
        Thread.currentThread().interrupt();
    }

    private void crawl(Website page) {
        if (!crawlAllowed(page)) return;

        markLinkAsVisited(page);
        logPageCrawling(page);

        Website fetchedPage;
        try {
            fetchedPage = fetchWebsite(page);
        } catch (Exception e) {
            return;
        }

        processFetchedPage(fetchedPage);
    }

    // checks if crawling is allowed depending on domain, depth and already visited links
    private boolean crawlAllowed(Website page) {
        if (visitedLinks.contains(page.getUrl())) {
            return false;
        }

        if (isMaxDepthReached(page)) {
            logger.info("[" + Thread.currentThread().getName() + "] Stopped crawling " + page.getUrl()
                    + " due to max depth reached (" + page.getDepth() + " > " + maxDepth + ").");
            return false;
        }

        if (!isDomainAllowed(page.getUrl())) {
            logger.info("[" + Thread.currentThread().getName() + "] Stopped crawling " + page.getUrl()
                    + " because domain is not allowed.");
            return false;
        }
        return true;
    }

    private boolean isMaxDepthReached(Website page) {
        return page.getDepth() > maxDepth;
    }

    private void markLinkAsVisited(Website page) {
        visitedLinks.add(page.getUrl());
    }

    private void logPageCrawling(Website page) {
        logger.info("[" + Thread.currentThread().getName() + "] Crawling URL: " + page.getUrl() + " at depth " + page.getDepth());
    }

    // crawls subpages
    private void processFetchedPage(Website page) {
        crawledPages.add(page);

        for (Website subPage : page.getSubPages()) {
            submitCrawlingTask(subPage);
        }
    }

    private Website fetchWebsite(Website page) throws Exception {
        return fetcher.fetch(page.getUrl(), page.getDepth());
    }

    // submits the task to the executor
    private void submitCrawlingTask(Website page) {
        activeTasks.incrementAndGet();
        executor.submit(() -> {
            try {
                crawl(page);
            } finally {
                taskDone();
            }
        });
    }


    // waits until all tasks of the executor are completed
    private void waitForCompletion() throws InterruptedException {
        boolean terminated = executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // checks if executor terminated safley
        if (!terminated) {
            logger.warning("Executor did not terminate properly.");
        }
    }

    // shuts the executor down
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
