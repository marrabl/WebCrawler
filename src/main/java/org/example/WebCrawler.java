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
        submitCrawlingTask(rootPage);

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
        if (!crawlAllowed(page)) return;

        markAsVisited(page);
        logPageCrawling(page);

        Website fetchedPage;
        try {
            fetchedPage = fetchWebsite(page);
        } catch (Exception e) {
            return;
        }

        processFetchedPage(fetchedPage);
    }

    private boolean crawlAllowed(Website page) {
        return isEligibleForVisit(page) && !visitedLinks.contains(page.getUrl());
    }

    private void markAsVisited(Website page) {
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
        try {
            return fetcher.fetch(page.getUrl(), page.getDepth());

        } catch (Exception e) {
            handleException("[" + Thread.currentThread().getName() + "] Error fetching website: " + page.getUrl(), e);
            throw  new Exception("Website null", e);
        }
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
