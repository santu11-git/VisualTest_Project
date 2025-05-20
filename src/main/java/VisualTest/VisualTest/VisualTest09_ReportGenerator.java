package VisualTest.VisualTest;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class VisualTest09_ReportGenerator {

    public static void generateHTMLReport(List<String> diffImagePaths, String reportFolderPath) {
        String reportPath = reportFolderPath + "/VisualTest_Report_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".html";
        try (PrintWriter writer = new PrintWriter(new FileWriter(reportPath))) {
            writer.println("<html><head><title>Visual Testing Report</title></head><body>");
            writer.println("<h2>Visual Testing Differences</h2>");

            for (String path : diffImagePaths) {
                writer.println("<div style='margin-bottom:20px;'>");
                writer.println("<p><b>Mismatch:</b> " + new File(path).getName() + "</p>");
                writer.println("<img src='" + path + "' style='max-width:600px; border:1px solid #ccc;'/>");
                writer.println("</div>");
            }

            writer.println("</body></html>");
            System.out.println("HTML report generated at: " + reportPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

