package VisualTest.VisualTest;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class AITCGherkinTestCaseGenerator {

    public static void extractGherkinText(String url) {
        WebDriver driver = null;

        try {
            // Step 1: Launch browser and go to page
            driver = new ChromeDriver();
            driver.manage().window().maximize();
            driver.get(url);
            Thread.sleep(3000); // Optional wait

            // Step 2: Extract DOM elements
            AITCUIElementExtractor extractor = new AITCUIElementExtractor();
            List<AITCUIElementMeta> domElements = extractor.extract(driver);

            // Step 3: Call Mistral client to generate Gherkin
            AITCMistralGherkinClient.callMistralToGenerateGherkin(domElements);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }
    }
}
