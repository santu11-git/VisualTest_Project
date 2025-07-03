package testjava;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

import VisualTest.VisualTest.A11YMistralScorerUtil;
import VisualTest.VisualTest.A11YUtil;
import VisualTest.VisualTest.A11YViolationToJsonExporter;
import VisualTest.VisualTest.ActualScreenshot;
import VisualTest.VisualTest.ChatGPTClient;
import VisualTest.VisualTest.FullPageRobotScreenshotUtil;
import VisualTest.VisualTest.FullPageScreenshotActualUtil;
import VisualTest.VisualTest.FullPageScreenshotBaselineUtil;
import VisualTest.VisualTest.GenAIA11YReportGenerator;
import VisualTest.VisualTest.HybridA11YVisionConnector;
import VisualTest.VisualTest.HybridTextJsonExporter;
import VisualTest.VisualTest.ImageComparisonUpdatedUtil;
import VisualTest.VisualTest.ImageComparisonUtil;
import VisualTest.VisualTest.ImageMultipleComparisonUtil;
import VisualTest.VisualTest.ScreenshotCaptureSessionTracker;
import VisualTest.VisualTest.VisionAIUtil;
import VisualTest.VisualTest.VisualAnomalyDetector;
import VisualTest.VisualTest.VisualComparisonBatchUtil;
import dev.failsafe.internal.util.Assert;
import org.testng.asserts.*;
import org.testng.*;


public class TestCase_03 {
	
	// This is the final A11Y - Issue detecting logic - 
		@Test 
		public void A11YTesting() throws Exception {
			
			WebDriver driver = new ChromeDriver();
			driver.get("https://soffront.com/");
			Thread.sleep(1000);
			driver.manage().window().maximize();
			A11YUtil.analyzePageAccessibility(driver);
			// Google Vision AI part - need billing to continue. 
			HybridA11YVisionConnector.processA11YScreenshotsWithVisionAI();
			// Assume your A11YUtil is already generating timestamp & folderPath
			
			Thread.sleep(3000);
			driver.quit();
		}
		
		@Test
		public void A11YTestingMultiplePage () throws Exception {
		    WebDriver driver = new ChromeDriver();
		    driver.manage().window().maximize();
		    
		    // ✅ Open Home Page
		    driver.get("https://soffront.com/");
		    Thread.sleep(2000); // allow page to load
		    A11YUtil.analyzePageAccessibility(driver);

		    // Navigate to next page:
		    driver.findElement(By.xpath("(//a[text()='Request More Info'])[1]")).click();
			driver.manage().window().maximize();
			A11YUtil.analyzePageAccessibility(driver);
			Thread.sleep(2000);
		    driver.quit();
		}
		
		// JSON Converter: 
		
		@Test
		public void exportA11YHybridTextReportToJson () throws Exception {
		
			String jsonPath = 	A11YViolationToJsonExporter.exportLatestA11YViolationToJson();
			System.out.println("📄 JSON file created at: " + jsonPath);

		}
		
		// Mistral AI Analysis:  
		
		@Test
		public void MistralA11YScoreAnalysis () throws Exception {
		String jsonPath = "src/main/resources/output/A11Y_Violations_20250628_164138/A11Y_Violations_20250628_164138.json";
		 if (jsonPath == null || jsonPath.isEmpty()) {
		        System.err.println("❌ No A11Y JSON file was exported. Please check the output folder or Excel file availability.");
		        return;
		    }
		A11YMistralScorerUtil.callMistralForA11Y(jsonPath);
		}

}
