package org.example;

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
                boolean isAccessible = page.getHeadingsByLevel() != null;
                String output = formatOutput(page, isAccessible);

                writer.write(output);
                writer.newLine();
                writer.flush();

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while writing report", e);
            }
        }
    }

    private String formatOutput(Website page, boolean isAccessible) {
        StringBuilder output = new StringBuilder();
        String depthArrow = "-->".repeat(page.getDepth());

        if (isAccessible) {
            output.append("<br>")
                    .append(depthArrow)
                    .append(" link to <a>")
                    .append(page.getUrl()).append("</a>");

            output.append("\n<br>depth: ")
                    .append(page.getDepth());

            output.append("\n")
                    .append(formatHeadings(page.getHeadingsByLevel(), page.getDepth()));
        } else {
            output.append("<br>")
                    .append(depthArrow)
                    .append(" broken link <a>")
                    .append(page.getUrl()).append("</a>");
        }

        return output.toString();
    }

    private String formatHeadings(Map<Integer, List<String>> headingsByLevel, int depth) {
        if (headingsByLevel == null) return "";
        StringBuilder result = new StringBuilder();
        String baseIndent = "# ".repeat(depth);

        for (int level = 1; level <= 6; level++) {
            List<String> headings = headingsByLevel.get(level);
            if (headings != null) {
                for (String heading : headings) {
                    result.append("\n")
                            .append(baseIndent)
                            .append("#".repeat(level))
                            .append(" ")
                            .append(heading);
                }
            }
        }
        return result.toString().trim();
    }
}
