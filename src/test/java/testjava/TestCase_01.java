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
import VisualTest.VisualTest.FullPageScreenshotUtil;
import VisualTest.VisualTest.GenAIA11YReportGenerator;
import VisualTest.VisualTest.HybridA11YVisionConnector;
import VisualTest.VisualTest.ImageComparisonUtil;
import VisualTest.VisualTest.ImageMultipleComparisonUtil;
import VisualTest.VisualTest.VisionAIUtil;
import VisualTest.VisualTest.VisualAnomalyDetector;
import VisualTest.VisualTest.VisualTesting10_ElementwiseSS;


public class TestCase_01 {
	@Test 
	public void GPTResponse() {
        ChatGPTClient gpt = new ChatGPTClient();
        String reply = gpt.getChatCompletion("Write a test scenario for user login with valid credentials.");
        System.out.println("gpt-4o-mini:\n" + reply);
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
	@Test
	public void TakeBaselineSSElementwise() throws Exception {
		
		WebDriver driver = new ChromeDriver();
		driver.get("https://www.way2automation.com/lifetime-membership-club/");
		driver.manage().window().maximize();
		driver.manage().window().setSize(new Dimension(1920, 1080)); 
		WebElement SS1= driver.findElement(By.xpath("(//h4[@class='elementor-heading-title elementor-size-default'])[5]"));
		VisualTesting10_ElementwiseSS.captureElementScreenshot(driver, SS1);
		driver.quit();
	}
	
	// This is for Baseline SS capture - Single SS (Not for Multiple Page in one Cycle):
	@Test 
	public void takeFullPageBaselineScreenshotBaseline() throws Exception {
		WebDriver driver = new ChromeDriver();
		driver.get("https://sauce-demo.myshopify.com/");
		driver.manage().window().maximize();
		// For Single Page:
		/*FullPageScreenshotUtil.captureFullPageScreenshotBaselineforMultiImage(driver, "baselineScreenshots");
		driver.quit();
        */
		//For Multiple Page:
		
		List<String> pages = Arrays.asList("page_01", "page_02");
		// Page 1
		FullPageScreenshotUtil.captureMultipleScreenshotsBaseline(pages, driver, "Page_1");

		// Page 2
		driver.findElement(By.xpath("(//a[text()='Search'])[1]")).click();// navigate to next page
		Thread.sleep(3000);
		FullPageScreenshotUtil.captureMultipleScreenshotsBaseline(pages, driver, "Page_2");

		driver.quit();

	}
	
	// This is for Actual SS capture - Single SS (Not for Multiple Page in one Cycle):
	@Test 
	public void takeFullPageBaselineScreenshotActual() throws Exception {
		WebDriver driver = new ChromeDriver();
		driver.get("https://sauce-demo.myshopify.com/");
		driver.manage().window().maximize();
		// For Single Screenshots
		/*
		 * ActualScreenshot.captureFullPageScreenshotActual(driver,
		 * "baselineScreenshots"); driver.quit();
		 */
		List<String> pages = Arrays.asList("page_01", "page_02");
		
		//Page-1
		// For Multiple Page:
		ActualScreenshot.captureMultipleScreenshotsActual(pages, driver, "page_01");
		// Page 2
				driver.findElement(By.xpath("(//a[text()='Search'])[1]")).click();// navigate to next page
				Thread.sleep(3000);
				ActualScreenshot.captureMultipleScreenshotsActual(pages, driver, "page_02");
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
	
	// This is for Comparison (Baseline vs Actual) - Single SS (Not for Multiple Page in one Cycle):
	@Test
	public void VisualTestRunner() throws Exception {
		
		 boolean result = ImageComparisonUtil.compareLatestBaselineAndActual();

	        if (result) {
	            System.out.println("Visual Test Passed!");
	        } else {
	            System.out.println("Visual Test Failed: Differences found.");
	        }
	    
	}
	
	// This is for Comparison (Baseline vs Actual) - Multiple SS (Not for single SS):
	@Test
	public void VisualTestRunnerMultipleImage() throws Exception {
	 ImageMultipleComparisonUtil.compareAllMatchingImages();
	}
	
	
	@Test 
	public void A11YTesting() throws Exception {
		
		WebDriver driver = new ChromeDriver();
		driver.get("https://tutorialsninja.com/demo/");
		Thread.sleep(1000);
		driver.manage().window().maximize();
		A11YUtil.analyzePageAccessibility(driver, "output");
		// Google Vision AI part - need billing to continue. 
		HybridA11YVisionConnector.processA11YScreenshotsWithVisionAI("output");
	
		
		// Assume your A11YUtil is already generating timestamp & folderPath
		
		Thread.sleep(3000);
		driver.quit();
	}
	
	
}


