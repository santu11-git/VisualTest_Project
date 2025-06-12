package VisualTest.VisualTest;

import com.deque.axe.AXE;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class A11YUtil {

    private static final URL AXE_SCRIPT_URL = A11YUtil.class.getResource("/A11Y/axe.min.js");

    public static void analyzePageAccessibility(WebDriver driver, String screenshotBasePath) {
        try {
            // Dynamic full page scroll to load lazy-loaded elements
            fullyScrollPage(driver);

            JSONObject responseJSON = new AXE.Builder(driver, AXE_SCRIPT_URL).analyze();
            JSONArray violations = responseJSON.getJSONArray("violations");

            

            if (violations.length() == 0) {
                System.out.println("✅ No accessibility violations found.");
                return;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File screenshotFolder = new File(screenshotBasePath + File.separator + "A11Y_Violations_" + timestamp);
            screenshotFolder.mkdirs();
            
         // Save violations to JSON
            A11YViolationJsonWriter.writeViolationsToJson(violations, screenshotBasePath, timestamp);
            A11YExcelReportGenerator.writeViolationsToExcel(violations, screenshotBasePath, timestamp);

            for (int i = 0; i < violations.length(); i++) {
                JSONObject violation = violations.getJSONObject(i);
                String rule = violation.getString("id");
                String impact = violation.optString("impact", "none");
                String description = violation.getString("description");
                String helpUrl = violation.getString("helpUrl");
                JSONArray nodes = violation.getJSONArray("nodes");

                String label = "violation_" + String.format("%02d", i + 1);
                System.out.println("========== " + label + " ==========");
                System.out.println("Rule: " + rule);
                System.out.println("Impact: " + impact);
                System.out.println("Description: " + description);
                System.out.println("Help URL: " + helpUrl);

                if (nodes.length() > 0) {
                    JSONObject node = nodes.getJSONObject(0);
                    JSONArray targets = node.getJSONArray("target");

                    if (targets.length() > 0) {
                        String cssSelector = targets.getString(0);

                        try {
                            WebElement element = driver.findElement(By.cssSelector(cssSelector));
                            scrollElementToCenter(driver, element);  // Center scroll
                            highlightElement(driver, element);

                           // STOP: Printing heavy HTML
							/*
							 * String html = element.getAttribute("outerHTML");
							 * System.out.println("Violating HTML: " + html);
							 */

                            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                            File dest = new File(screenshotFolder, label + "_" + timestamp + ".png");
                            FileHandler.copy(src, dest);
                            System.out.println("Screenshot saved to: " + dest.getAbsolutePath());
                        } catch (Exception e) {
                            System.out.println("⚠️ Could not locate or capture element: " + cssSelector);
                        }
                    }
                }
                System.out.println("======================================\n");
            }

        } catch (Exception e) {
            System.out.println("❌ Error during accessibility scan: " + e.getMessage());
        }
    }

    /**
     * Dynamically scrolls page fully to handle lazy loaded content.
     */
    private static void fullyScrollPage(WebDriver driver) {
        try {
            long lastHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
            int sameHeightCount = 0;

            while (sameHeightCount < 3) {
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(1000); // wait for loading

                long newHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");

                if (newHeight == lastHeight) {
                    sameHeightCount++;
                } else {
                    sameHeightCount = 0;  // reset counter if height changes
                }
                lastHeight = newHeight;
            }

            // Final return to top for cleaner screenshots
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("⚠️ Error while full scrolling: " + e.getMessage());
        }
    }

    /**
     * Scrolls element to center of viewport.
     */
    private static void scrollElementToCenter(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "const viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);" +
                "const elementTop = arguments[0].getBoundingClientRect().top;" +
                "window.scrollBy(0, elementTop - (viewPortHeight/2));", element);
        try { Thread.sleep(800); } catch (InterruptedException e) {}
    }

    /**
     * Highlights element with red border.
     */
    private static void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red'", element);
    }
}
