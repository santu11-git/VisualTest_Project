package VisualTest.VisualTest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;

public class SEOTestUtil {

    // Report class holds the results and status
    public static class SeoReport {
        public String title;
        public String metaDescription;
        public int h1Count;
        public String canonicalUrl;
        public List<String> missingAltImages = new ArrayList<>();

        public boolean titlePresent;
        public boolean descriptionPresent;
        public boolean h1Present;
        public boolean canonicalPresent;

        public boolean passed;              // Status for passing/failing
        public String summaryMessage;       // Summary message for UI or console
    }

    // Main method to be called by the QA engineers
    public static SeoReport validateSEO(WebDriver driver) {
        SeoReport report = generateSeoReport(driver);
        evaluatePassFail(report);           // Sets status + summary
        return report;                      // Return the report object
    }

    // Method to generate SEO report by checking title, meta, h1, etc.
    private static SeoReport generateSeoReport(WebDriver driver) {
        SeoReport report = new SeoReport();
        Document doc = Jsoup.parse(driver.getPageSource());

        // Title check
        report.title = doc.title();
        report.titlePresent = report.title != null && !report.title.isEmpty();

        // Meta Description check
        Element descElem = doc.selectFirst("meta[name=description]");
        report.metaDescription = descElem != null ? descElem.attr("content") : null;
        report.descriptionPresent = report.metaDescription != null && !report.metaDescription.isEmpty();

        // H1 check
        report.h1Count = doc.select("h1").size();
        report.h1Present = report.h1Count > 0;

        // Canonical URL check
        Element canonicalElem = doc.selectFirst("link[rel=canonical]");
        report.canonicalUrl = canonicalElem != null ? canonicalElem.attr("href") : null;
        report.canonicalPresent = report.canonicalUrl != null && !report.canonicalUrl.isEmpty();

        // Missing Alt check (for images)
        for (Element img : doc.select("img")) {
            String src = img.attr("src");
            boolean isTrackingPixel = src.contains("ads") || src.contains("facebook") || src.contains("linkedin");

            // Skip known tracking pixel images
            if (!img.hasAttr("alt") || img.attr("alt").trim().isEmpty()) {
                if (!isTrackingPixel) {
                    report.missingAltImages.add(src);
                }
            }
        }

        return report;
    }

    // Method to evaluate if the report passed or failed
    private static void evaluatePassFail(SeoReport report) {
        boolean hasBasicFields =
            report.titlePresent &&
            report.descriptionPresent &&
            report.h1Present &&
            report.canonicalPresent;

        boolean hasAltIssues = !report.missingAltImages.isEmpty();

        // Determine if test passed or failed
        report.passed = hasBasicFields && !hasAltIssues;

        // Prepare summary message
        if (report.passed) {
            report.summaryMessage = "✅ SEO check passed with no violations.";
        } else {
            StringBuilder msg = new StringBuilder("❌ SEO check failed due to:");
            if (!report.titlePresent) msg.append(" [Missing Title]");
            if (!report.descriptionPresent) msg.append(" [Missing Meta Description]");
            if (!report.h1Present) msg.append(" [No H1]");
            if (!report.canonicalPresent) msg.append(" [Missing Canonical]");
            if (hasAltIssues) msg.append(" [Images missing ALT text]");
            report.summaryMessage = msg.toString();
        }
    }

    // Method to log the SEO report to the console (or for debugging)
    public static void logSEOReport(SeoReport report, String pageLabel) {
        System.out.println("=========== SEO REPORT: " + pageLabel + " ===========");
        System.out.println("🔎 Summary: " + report.summaryMessage);
        System.out.println("✅ Title: " + report.title + " (Present: " + report.titlePresent + ")");
        System.out.println("✅ Meta Description: " + report.metaDescription + " (Present: " + report.descriptionPresent + ")");
        System.out.println("✅ H1 Count: " + report.h1Count + " (Present: " + report.h1Present + ")");
        System.out.println("✅ Canonical URL: " + report.canonicalUrl + " (Present: " + report.canonicalPresent + ")");
        System.out.println("🖼️ Images missing ALT: " + report.missingAltImages.size());
        report.missingAltImages.forEach(img -> System.out.println("   ↪ " + img));
        System.out.println("===================================");
    }

    
}
