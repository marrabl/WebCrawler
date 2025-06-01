package org.example;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        if (args.length < 3) {
            logger.info("Usage: java Main <url> <depth> <allowedDomains (comma-separated)>");
            return;
        }

        String url = args[0];
        int depth;

        try {
            depth = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Depth must be an integer.", e);
            return;
        }

        Set<String> allowedDomains = new HashSet<>(Arrays.asList(args[2].split(",")));

        logger.info("Starting crawler with URL: " + url + ", depth: " + depth + ", allowed domains: " + allowedDomains);

        WebCrawler crawler = new WebCrawler(url, depth, allowedDomains);
        crawler.run();
    }
}
