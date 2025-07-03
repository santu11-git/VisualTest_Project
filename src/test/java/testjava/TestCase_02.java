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
import org.openqa.selenium.support.ui.Select;
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
import VisualTest.VisualTest.ScreenshotCaptureSessionTracker;
import VisualTest.VisualTest.VisionAIUtil;
import VisualTest.VisualTest.VisualAnomalyDetector;
import VisualTest.VisualTest.VisualComparisonBatchUtil;
import dev.failsafe.internal.util.Assert;
import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.asserts.*;
import org.testng.*;

public class TestCase_02 {
	
	// Test Case_01: URL: https://tutorialsninja.com/demo/
	@Test 
	public void takeFullPageBaselineScreenshotMultiplePages () throws Exception {
		
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		// * Open Home Page: Page_1
		driver.get("https://tutorialsninja.com/demo/");
		driver.manage().window().maximize();
		FullPageScreenshotBaselineUtil.captureScreenshotForMultipleCurrentPageBaseline(driver, "baseline", "Page1");
		Thread.sleep(5000);
		
		// Navigate to Next Page_2:
		
		WebElement dropDown = driver.findElement(By.xpath("(//a[@class='dropdown-toggle'])[2]"));
		dropDown.click();
		driver.findElement(By.xpath("(//a[@class='see-all'])[1]")).click();
		FullPageScreenshotBaselineUtil.captureScreenshotForMultipleCurrentPageBaseline(driver, "baseline", "Page2");
		Thread.sleep(5000);
		
		// Navigate to Next Page_3:
		
		driver.findElement(By.xpath("(//a[text()='Software'])[1]")).click();
		FullPageScreenshotBaselineUtil.captureScreenshotForMultipleCurrentPageBaseline(driver, "baseline", "Page3");
		Thread.sleep(5000);
				
		driver.quit();
	}
	
	@Test 
	public void takeFullPageActualScreenshotMultiplePages () throws Exception {
		
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		// * Open Home Page: Page_1
		driver.get("https://tutorialsninja.com/demo/");
		driver.manage().window().maximize();
		FullPageScreenshotActualUtil.captureScreenshotForMultipleCurrentPageActual(driver, "actual", "Page1");
		Thread.sleep(5000);
		
		// Navigate to Next Page_2:
		
		WebElement dropDown = driver.findElement(By.xpath("(//a[@class='dropdown-toggle'])[2]"));
		dropDown.click();
		driver.findElement(By.xpath("(//a[@class='see-all'])[1]")).click();
		FullPageScreenshotActualUtil.captureScreenshotForMultipleCurrentPageActual(driver, "actual", "Page2");
		Thread.sleep(5000);
		
		// Navigate to Next Page_3:
		
		driver.findElement(By.xpath("(//a[text()='Software'])[1]")).click();
		FullPageScreenshotActualUtil.captureScreenshotForMultipleCurrentPageActual(driver, "actual", "Page3");
		Thread.sleep(5000);
				
		driver.quit();
		
	}
		@Test
		public void VisualTestRunnerMultipleImage() throws Exception {
		 
			VisualComparisonBatchUtil.compareLatestMultipleScreenshots();
		}
		
		// Test Case_02: URL: https://sauce-demo.myshopify.com/
		
		// This is for Baseline SS capture - for Multiple Page in one Cycle:
		@Test
		public void takeFullPageBaselineScreenshotMultiplePages1() throws Exception {
		    WebDriver driver = new ChromeDriver();
		    driver.manage().window().maximize();
		    ScreenshotCaptureSessionTracker.resetTimestamp(); // Optional but recommended for fresh run
		    // ✅ Open Home Page
		    driver.get("https://sauce-demo.myshopify.com/");
		    Thread.sleep(2000); // allow page to load
		    FullPageScreenshotBaselineUtil.captureScreenshotForMultipleCurrentPageBaseline(driver, "baseline", "Page_01");

		    // ✅ Navigate to Search Page
		    driver.findElement(By.xpath("(//a[text()='Search'])[1]")).click();
		    Thread.sleep(3000);
		    FullPageScreenshotBaselineUtil.captureScreenshotForMultipleCurrentPageBaseline(driver, "baseline", "Page_02");

		    driver.quit();
		}
		
		@Test
		public void takeFullPageActualScreenshotMultiplePages1() throws Exception {
		    WebDriver driver = new ChromeDriver();
		    driver.manage().window().maximize();
		    ScreenshotCaptureSessionTracker.resetTimestamp(); // Optional but recommended for fresh run
		    // ✅ Open Home Page
		    driver.get("https://sauce-demo.myshopify.com/");
		    Thread.sleep(2000); // allow page to load
		    FullPageScreenshotActualUtil.captureScreenshotForMultipleCurrentPageActual(driver, "actual", "Page_01");

		    // ✅ Navigate to Search Page
		    driver.findElement(By.xpath("(//a[text()='Search'])[1]")).click();
		    Thread.sleep(3000);
		    FullPageScreenshotActualUtil.captureScreenshotForMultipleCurrentPageActual(driver, "actual", "Page_02");

		    driver.quit();
		}
		
		@Test
		public void VisualTestRunnerMultipleImage1() throws Exception {
		
			VisualComparisonBatchUtil.compareLatestMultipleScreenshots();
		}
		
		
	}		
	


