package testjava;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import VisualTest.VisualTest.SEOTestUtil;
import VisualTest.VisualTest.SEOTestUtil.SeoReport;

public class TestCase_05 {
	
	// This is the final SEO - Issue detecting logic - 
			@Test 
			public void testSEOAuditForHomePage() throws Exception {
				
				WebDriver driver = new ChromeDriver();
				driver.get("https://soffront.com/");
				Thread.sleep(1000);
				driver.manage().window().maximize();
				SeoReport report = SEOTestUtil.validateSEO(driver);  // Only this method needs to be called  // SEO Testing Method Call.
				
				JavascriptExecutor js = (JavascriptExecutor) driver;
			    js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
			    
			 
			// Log the report for console output or UI integration
			  SEOTestUtil.logSEOReport(report, "Home Page");
				
				Thread.sleep(3000);
				driver.quit();
			}
			
			@Test 
			public void testSEOAuditForBatchProcess() throws Exception {
				
				WebDriver driver = new ChromeDriver();
				driver.get("https://soffront.com/");
				Thread.sleep(1000);
				driver.manage().window().maximize();
				SeoReport report = SEOTestUtil.validateSEO(driver);  // Only this method needs to be called  // SEO Testing Method Call.
				
				JavascriptExecutor js = (JavascriptExecutor) driver;
			    js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
			    
			 
			// Log the report for console output or UI integration
			  SEOTestUtil.logSEOReport(report, "Home Page");
			  
			  driver.findElement(By.xpath("(//a[text()='Request More Info'])[1]")).click();
			  driver.manage().window().maximize();
				SeoReport report1 = SEOTestUtil.validateSEO(driver);  // Only this method needs to be called  // SEO Testing Method Call.
				
				JavascriptExecutor js1 = (JavascriptExecutor) driver;
			    js1.executeScript("window.scrollBy(0,document.body.scrollHeight)");
			    SEOTestUtil.logSEOReport(report1, "Blog Page");
				Thread.sleep(3000);
				driver.quit();
			}

}
