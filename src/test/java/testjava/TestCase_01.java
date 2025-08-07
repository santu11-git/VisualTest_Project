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

import VisualTest.VisualTest.A11YUtil;
import VisualTest.VisualTest.ActualScreenshot;
import VisualTest.VisualTest.ChatGPTClient;
import VisualTest.VisualTest.FullPageRobotScreenshotUtil;
import VisualTest.VisualTest.FullPageScreenshotActualUtil;
import VisualTest.VisualTest.FullPageScreenshotBaselineUtil;
import VisualTest.VisualTest.GenAIA11YReportGenerator;
import VisualTest.VisualTest.HybridA11YVisionConnector;
import VisualTest.VisualTest.ImageComparisonUpdatedUtil;
import VisualTest.VisualTest.ImageComparisonUtil;
import VisualTest.VisualTest.ImageMultipleComparisonUtil;
import VisualTest.VisualTest.ResponsiveCDPUtil;
import VisualTest.VisualTest.SingleImageZipUtil;
import VisualTest.VisualTest.VisionAIUtil;
import VisualTest.VisualTest.VisualAnomalyDetector;
import VisualTest.VisualTest.VisualComparisonBatchUtil;
import dev.failsafe.internal.util.Assert;
import org.testng.asserts.*;
import org.testng.*;

public class TestCase_01 {
	@Test 
	public void GPTResponse() {
        ChatGPTClient gpt = new ChatGPTClient();
        String reply = gpt.getChatCompletion("Write a test scenario for user login with valid credentials.");
        System.out.println("gpt-4o-mini:\n" + reply);
        System.out.println();
    }
	
	@Test

	public void validateLoginPageVisuals() throws InterruptedException {
	    WebDriver driver = new ChromeDriver();
	    driver.get("https://www.saucedemo.com/");
	    driver.manage().window().maximize();
	    // Vision (image) analysis
	    VisualAnomalyDetector.detectImageVisualAnomalies(driver);

	    // Text-based anomaly check
	    WebElement username = driver.findElement(By.id("user-name"));
	    WebElement password = driver.findElement(By.id("password"));
	    WebElement loginBtn = driver.findElement(By.id("login-button"));
	  //  VisualAnomalyDetector.detectTextualVisualAnomalies(username, password, loginBtn);

	    driver.quit();
	}
	
	// This is for Baseline SS capture - Single SS (Not for Multiple Page in one Cycle):
	@Test 
	public void takeFullPageScreenshotBaseline() throws Exception {
		WebDriver driver = new ChromeDriver();
		driver.get("https://sauce-demo.myshopify.com/");
		// ResponsiveCDPUtil.setMobileView(driver);
		Thread.sleep(5000);
		driver.manage().window().maximize();
		// For Single Page:
		FullPageScreenshotBaselineUtil.captureFullPageScreenshotBaseline(driver);
		driver.quit();

	}
	
	// This is for Actual SS capture - Single SS (Not for Multiple Page in one Cycle):
	@Test 
	public void takeFullPageScreenshotActual() throws Exception {
		WebDriver driver = new ChromeDriver();
		driver.get("https://sauce-demo.myshopify.com/");
		//ResponsiveCDPUtil.setMobileView(driver);
		Thread.sleep(5000);
		driver.manage().window().maximize();
		// For Single Screenshots
		
		 ActualScreenshot.captureFullPageScreenshotActual(driver); 
		 driver.quit();
	}
	
	// This is not applicable: 
	@Test
	public void FullPageRobotScreenshot() throws Exception {
		WebDriver driver = new ChromeDriver();
		driver.get("https://sauce-demo.myshopify.com/");
		driver.manage().window().maximize();
		FullPageRobotScreenshotUtil.captureFullPageScreenshot(driver, "fullpage_screenshots");
		driver.quit();
	}
	
	// This is for Comparison (Baseline vs Actual) - Single SS (Not for Multiple Page in one Cycle): old one
	@Test
	public void VisualTestRunner() throws Exception {
		
		 boolean result = ImageComparisonUtil.compareLatestBaselineAndActual();

	        if (result) {
	            System.out.println("Visual Test Passed!");
	        } else {
	            System.out.println("Visual Test Failed: Differences found.");
	        }
	    
	}
	
	// This is the final Baseline vs Actual comparison Test Case: New One - Single Page
	@Test
	public void VisualTestRunnerUpdated () throws Exception {
		
		boolean comparisonResult = ImageComparisonUpdatedUtil.compareLatestBaselineAndActual();

        if (comparisonResult==true) {
            System.out.println("✅ Visual Test Passed: Images are visually similar.");
         } 
        else {
            System.out.println("❌ Visual Test Failed: Differences found.");
            // Fail the test in TestNG if any difference found
         }  
        
        String zipFilePath= SingleImageZipUtil.createLatestVisualIQZip(); // Automatically zip latest folders on failure
	}
	
	
}


