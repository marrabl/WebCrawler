package org.example.Writer;

import org.example.Website;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReportWriter implements Writer {

    private static final Logger logger = Logger.getLogger(ReportWriter.class.getName());

    private final BufferedWriter writer;

    public ReportWriter() throws IOException {
        this.writer = new BufferedWriter(new FileWriter("report.md"));
    }

    public void write(List<Website> pages) {
        pages.sort(Comparator.comparingInt(Website::getDepth));

        for (Website page : pages) {
            try {
                String output = formatOutput(page);

                writer.write(output);
                writer.newLine();
                writer.flush();

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while writing report", e);
            }
        }
    }

    private String formatOutput(Website page) {
        // start with whitespace to make the report look consistent
        final String indent = "      ";

        StringBuilder sb = new StringBuilder();

        if (page.isReachable()) {
            sb.append(indent)
                    .append("- [link](")
                    .append(page.getUrl())
                    .append(") (depth: ")
                    .append(page.getDepth())
                    .append(")\n");

            Map<Integer, List<String>> headings = page.getHeadingsByLevel();
            if (headings != null && !headings.isEmpty()) {
                sb.append(indent).append("Headings:\n");

                for (int level = 1; level <= 6; level++) {
                    List<String> levelHeadings = headings.get(level);
                    if (levelHeadings != null) {
                        for (String heading : levelHeadings) {
                            sb.append(indent)
                                    .append("#".repeat(level))
                                    .append(" ")
                                    .append(heading)
                                    .append("\n");
                        }
                    }
                }
            }

        } else {
            sb.append(indent)
                    .append("- [Broken Link](")
                    .append(page.getUrl())
                    .append(") (depth: ")
                    .append(page.getDepth())
                    .append(")\n");
        }

        sb.append("\n");

        return sb.toString();
    }
}
