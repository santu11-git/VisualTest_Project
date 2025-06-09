package testjava;

import java.io.File;
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

import VisualTest.VisualTest.ActualScreenshot;
import VisualTest.VisualTest.ChatGPTClient;
import VisualTest.VisualTest.FullPageRobotScreenshotUtil;
import VisualTest.VisualTest.FullPageScreenshotUtil;
import VisualTest.VisualTest.ImageComparisonUtil;
import VisualTest.VisualTest.VisualAnomalyDetector;
import VisualTest.VisualTest.VisualTesting10_ElementwiseSS;


public class TestCase_01 {
	@Test 
	public void GPTResponse() {
        ChatGPTClient gpt = new ChatGPTClient();
        String reply = gpt.getChatCompletion("Write a username validation test cases ");
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
	
	@Test 
	public void takeFullPageBaselineScreenshotBaseline() throws Exception {
		WebDriver driver = new ChromeDriver();
		driver.get("https://sauce-demo.myshopify.com/");
		driver.manage().window().maximize();
		FullPageScreenshotUtil.captureFullPageScreenshotBaseline(driver, "baselineScreenshots");
		driver.quit();
	}
	
	@Test 
	public void takeFullPageBaselineScreenshotActual() throws Exception {
		WebDriver driver = new ChromeDriver();
		driver.get("https://sauce-demo.myshopify.com/");
		driver.manage().window().maximize();
		ActualScreenshot.captureFullPageScreenshotActual(driver, "baselineScreenshots");
		driver.quit();
	}
	
	@Test
	public void FullPageRobotScreenshot() throws Exception {
		WebDriver driver = new ChromeDriver();
		driver.get("https://sauce-demo.myshopify.com/");
		driver.manage().window().maximize();
		FullPageRobotScreenshotUtil.captureFullPageScreenshot(driver, "fullpage_screenshots");
		driver.quit();
	}
	
	@Test
	public void VisualTestRunner() throws Exception {
		
		 boolean result = ImageComparisonUtil.compareLatestBaselineAndActual();

	        if (result) {
	            System.out.println("🎯 Visual Test Passed!");
	        } else {
	            System.out.println("🛑 Visual Test Failed: Differences found.");
	        }
	    
	}
	
}


