package org.example;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java Main <url> <depth> <allowedDomains (comma-separated)>");
            return;
        }

        String url = args[0];
        int depth = Integer.parseInt(args[1]);
        List<String> domains = Arrays.asList(args[2].split(","));

        WebCrawler crawler = new WebCrawler(url, depth, domains);
        crawler.run();
    }
}