package VisualTest.VisualTest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class A11YHtmlReportGenerator {

    public static void writeViolationsToHtml(JSONArray violations, String outputDir, String timestamp, String pageUrl) {
        try {
            String reportName = "A11Y_Report_" + timestamp + ".html";
            File reportFile = new File(outputDir, reportName);

            // Count impacts for pie chart
            int critCount = 0, seriousCount = 0, moderateCount = 0, minorCount = 0;
            for (int i = 0; i < violations.length(); i++) {
                JSONObject violation = violations.getJSONObject(i);
                String impact = violation.optString("impact", "minor").toLowerCase();
                switch (impact) {
                    case "critical": critCount++; break;
                    case "serious": seriousCount++; break;
                    case "moderate": moderateCount++; break;
                    case "minor": default: minorCount++; break;
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {

                // HTML header with CSS
                writer.write("<html><head><meta charset='UTF-8'>");
                writer.write("<title>Accessibility Report</title>");
                writer.write("<style>");
                writer.write("body { font-family: Arial, sans-serif; margin: 20px; }");
                writer.write("h1 { color: #2E86C1; }");
                writer.write("table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }");
                writer.write("th, td { border: 1px solid #ddd; padding: 8px; }");
                writer.write("th { background-color: #2E86C1; color: white; }");
                writer.write(".impact-crit { color: red; font-weight: bold; }");
                writer.write(".impact-serious { color: orange; font-weight: bold; }");
                writer.write(".impact-moderate { color: #D68910; font-weight: bold; }");
                writer.write(".impact-minor { color: gray; }");
                writer.write(".banner { background-color: #ffcc00; padding: 10px; margin-bottom: 20px; font-weight: bold; }");
                writer.write(".top-section { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 10px; }");
                writer.write(".info { flex: 1; }");
                writer.write(".chart { width: 200px; height: 200px; }");
                writer.write("</style>");
                writer.write("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
                writer.write("</head><body>");

                // ⚠ Banner
                writer.write("<div class='banner'>⚠ Please extract this folder to view screenshots properly.</div>");
                writer.write("<h1>Accessibility Violations Report</h1>");

                // ✅ Compact top layout (info + chart side by side)
                writer.write("<div class='top-section'>");

                // Info block (Generated, URL)
                writer.write("<div class='info'>");
                writer.write("<p><b>Generated:</b> " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "</p>");
                writer.write("<p><b>Applicable URL:</b> <a href='" + pageUrl + "' target='_blank'>" + pageUrl + "</a></p>");
                writer.write("</div>");

                // Chart block (reduced size)
                writer.write("<div class='chart'>");
                writer.write("<canvas id='impactChart' width='200' height='200'></canvas>");
                writer.write("</div>");

                writer.write("</div>"); // close top-section

                if (violations.length() == 0) {
                    writer.write("<h2 style='color:green'>✅ No accessibility violations found.</h2>");
                } else {
                    // Pie Chart script
                    writer.write("<script>");
                    writer.write("const ctx = document.getElementById('impactChart').getContext('2d');");
                    writer.write("new Chart(ctx, {type:'pie', data:{");
                    writer.write("labels:['Critical','Serious','Moderate','Minor'],");
                    writer.write("datasets:[{data:[" + critCount + "," + seriousCount + "," + moderateCount + "," + minorCount + "],");
                    writer.write("backgroundColor:['#e74c3c','#e67e22','#f1c40f','#95a5a6']}]}," );
                    writer.write("options:{plugins:{legend:{position:'bottom'},},responsive:false}});");
                    writer.write("</script>");

                    // Violations Table
                    writer.write("<h2>Violations Found: " + violations.length() + "</h2>");
                    writer.write("<table>");
                    writer.write("<tr><th>#</th><th>Rule</th><th>Impact</th><th>Description</th><th>Help URL</th><th>Target</th><th>Screenshot</th></tr>");

                    for (int i = 0; i < violations.length(); i++) {
                        JSONObject violation = violations.getJSONObject(i);
                        String rule = violation.optString("id", "N/A");
                        String impact = violation.optString("impact", "N/A");
                        String description = violation.optString("description", "N/A");
                        String helpUrl = violation.optString("helpUrl", "#");

                        JSONArray nodes = violation.getJSONArray("nodes");
                        String target = nodes.length() > 0 ? nodes.getJSONObject(0).getJSONArray("target").toString() : "N/A";

                        String screenshotPath = "violations/violation_" + String.format("%02d", i + 1) + ".png";
                        File screenshotFile = new File(outputDir, screenshotPath);

                        writer.write("<tr>");
                        writer.write("<td>" + (i + 1) + "</td>");
                        writer.write("<td>" + rule + "</td>");
                        writer.write("<td class='impact-" + (impact != null ? impact.toLowerCase() : "minor") + "'>" + impact + "</td>");
                        writer.write("<td>" + description + "</td>");
                        writer.write("<td><a href='" + helpUrl + "' target='_blank'>Help Link</a></td>");
                        writer.write("<td>" + target + "</td>");
                        if (screenshotFile.exists()) {
                            writer.write("<td><a href='" + screenshotPath + "' target='_blank'>View Screenshot</a></td>");
                        } else {
                            writer.write("<td>N/A</td>");
                        }
                        writer.write("</tr>");
                    }

                    writer.write("</table>");
                }

                writer.write("</body></html>");
            }

            System.out.println("📄 HTML Report saved at: " + reportFile.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("⚠️ Error writing HTML report: " + e.getMessage());
        }
    }
}
