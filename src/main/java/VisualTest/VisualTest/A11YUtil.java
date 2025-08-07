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
import java.util.UUID;

public class A11YUtil {

    private static final URL AXE_SCRIPT_URL = A11YUtil.class.getResource("/A11Y/axe.min.js");

    public static String analyzePageAccessibility(WebDriver driver) {
        String baseOutputDir = null;
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            baseOutputDir = System.getProperty("java.io.tmpdir") + File.separator + "A11Y_Result_" + timestamp;
            File outputDir = new File(baseOutputDir);
            if (!outputDir.exists()) outputDir.mkdirs();

            fullyScrollPage(driver);

            JSONObject responseJSON = new AXE.Builder(driver, AXE_SCRIPT_URL).analyze();
            JSONArray violations = responseJSON.getJSONArray("violations");

            if (violations.length() == 0) {
                System.out.println("✅ No accessibility violations found.");
                return baseOutputDir;
            }

            File screenshotFolder = new File(baseOutputDir + File.separator + "violations");
            screenshotFolder.mkdirs();

            A11YViolationJsonWriter.writeViolationsToJson(violations, baseOutputDir, timestamp);
            A11YExcelReportGenerator.writeViolationsToExcel(violations, baseOutputDir, timestamp);

            for (int i = 0; i < violations.length(); i++) {
                JSONObject violation = violations.getJSONObject(i);
                JSONArray nodes = violation.getJSONArray("nodes");
                String label = "violation_" + String.format("%02d", i + 1);

                if (nodes.length() > 0) {
                    JSONObject node = nodes.getJSONObject(0);
                    JSONArray targets = node.getJSONArray("target");

                    if (targets.length() > 0) {
                        String cssSelector = targets.getString(0);

                        try {
                            WebElement element = driver.findElement(By.cssSelector(cssSelector));
                            scrollElementToCenter(driver, element);
                            highlightElement(driver, element);

                            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                            File dest = new File(screenshotFolder, label + ".png");
                            FileHandler.copy(src, dest);
                            System.out.println("🖼️ Screenshot saved to: " + dest.getAbsolutePath());

                        } catch (Exception e) {
                            System.out.println("⚠️ Could not locate or capture element: " + cssSelector);
                        }
                    }
                }
                System.out.println("======================================\n");
            }

            System.out.println("✅ Accessibility testing completed. Reports saved at: " + baseOutputDir);
            return baseOutputDir;

        } catch (Exception e) {
            System.out.println("❌ Error during accessibility scan: " + e.getMessage());
            return baseOutputDir;
        }
    }

    private static void fullyScrollPage(WebDriver driver) {
        try {
            long lastHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
            int sameHeightCount = 0;

            while (sameHeightCount < 3) {
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(1000);
                long newHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");

                if (newHeight == lastHeight) {
                    sameHeightCount++;
                } else {
                    sameHeightCount = 0;
                }
                lastHeight = newHeight;
            }
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("⚠️ Error while full scrolling: " + e.getMessage());
        }
    }

    private static void scrollElementToCenter(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "const viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);" +
                "const elementTop = arguments[0].getBoundingClientRect().top;" +
                "window.scrollBy(0, elementTop - (viewPortHeight/2));", element);
        try { Thread.sleep(800); } catch (InterruptedException e) {}
    }

    private static void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].style.border='3px solid red'", element);
    }

}
