package testjava;

import java.io.File;
import java.io.IOException;
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

import VisualTest.VisualTest.A11YAIResultDTO;
import VisualTest.VisualTest.A11YBugReportDTO;
import VisualTest.VisualTest.A11YBugReportUtil;
import VisualTest.VisualTest.A11YMistralScorerUtil;
import VisualTest.VisualTest.A11YUtil;
import VisualTest.VisualTest.A11YViolationToJsonExporter;
import VisualTest.VisualTest.A11YZipUtility;
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
			//A11YUtil.analyzePageAccessibility(driver);
			// Google Vision AI part - need billing to continue. 
			// HybridA11YVisionConnector.processA11YScreenshotsWithVisionAI();
			// Assume your A11YUtil is already generating timestamp & folderPath
			
			String latestReportFolder = A11YUtil.analyzePageAccessibility(driver);
		    String zipFilePath = latestReportFolder + ".zip";

		    A11YZipUtility.zipFolder(latestReportFolder, zipFilePath);

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
		
		//	String jsonPath = 	A11YViolationToJsonExporter.exportLatestA11YViolationToJson(jsonPath);
		//	System.out.println("📄 JSON file created at: " + jsonPath);

		}
		
		// Mistral AI Analysis:  
		
		@Test
		public void MistralA11YScoreAnalysis () throws Exception {
		String jsonPath = "src/main/resources/output/A11Y_Violation_Report_20250712_003441.json";
		 if (jsonPath == null || jsonPath.isEmpty()) {
		        System.err.println("❌ No A11Y JSON file was exported. Please check the output folder or Excel file availability.");
		        return;
		    }
	A11YMistralScorerUtil.callMistralForA11Y(jsonPath, jsonPath);
		}

		
		@Test
		public void testFullAIFlow() throws InterruptedException, IOException {
			WebDriver driver = new ChromeDriver();
			driver.get("https://soffront.com/");
			Thread.sleep(1000);
			driver.manage().window().maximize();
		     String outputDir = A11YUtil.analyzePageAccessibility(driver);

		    String jsonPath = A11YViolationToJsonExporter.exportLatestA11YViolationToJson(outputDir);
		    if (jsonPath == null) {
		        System.err.println("❌ No A11Y JSON generated. Skipping AI analysis.");
		        return;
		    }

		    A11YAIResultDTO aiResult = A11YMistralScorerUtil.callMistralForA11Y(jsonPath, outputDir);

		    System.out.println("\n=== Final AI Results ===");
		    System.out.println("Confidence Score: " + aiResult.confidenceScore);
		    System.out.println("Top 3 Violations:\n" + aiResult.top3Summary);
		    System.out.println("Badge: " + aiResult.badge);
		    System.out.println("AI JSON Path: " + aiResult.aiJsonPath);
		    
		    // Generate Bug Report (AI Based):
		    A11YBugReportDTO bugDTO = A11YBugReportUtil.generateBugReportExcel(jsonPath);
		    if (bugDTO != null) {
		        System.out.println("\n=== Final Bug Report ===");
		        System.out.println("Bug Report Path: " + bugDTO.bugReportPath);
		        System.out.println("Total Bugs: " + bugDTO.totalBugs);
		        System.out.println("Summary:" + bugDTO.summary);
		    }
		    
		    String zipFilePath = outputDir + ".zip";
		    A11YZipUtility.zipFolder(outputDir, zipFilePath);
		    
		    driver.quit();
		}
	}
