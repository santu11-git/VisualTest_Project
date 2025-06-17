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
import VisualTest.VisualTest.VisionAIUtil;
import VisualTest.VisualTest.VisualAnomalyDetector;
import VisualTest.VisualTest.VisualComparisonBatchUtil;
import VisualTest.VisualTest.VisualTesting10_ElementwiseSS;
import dev.failsafe.internal.util.Assert;
import org.testng.asserts.*;
import org.testng.*;


public class TestCase_03 {
	
	// This is the final A11Y - Issue detecting logic - 
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
