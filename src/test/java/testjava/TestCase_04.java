package testjava;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
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
import VisualTest.VisualTest.DomActualExcelWriter;
import VisualTest.VisualTest.DomTextComparator;
import VisualTest.VisualTest.DomTextExcelWriter;
import VisualTest.VisualTest.DomTextExtractor;
import VisualTest.VisualTest.FullPageRobotScreenshotUtil;
import VisualTest.VisualTest.FullPageScreenshotActualUtil;
import VisualTest.VisualTest.FullPageScreenshotBaselineUtil;
import VisualTest.VisualTest.GenAIA11YReportGenerator;
import VisualTest.VisualTest.HuggingFaceScorerUtil;
import VisualTest.VisualTest.HybridA11YVisionConnector;
import VisualTest.VisualTest.HybridDomOcrComparisonUtil;
import VisualTest.VisualTest.HybridTextJsonExporter;
import VisualTest.VisualTest.ImageComparisonUpdatedUtil;
import VisualTest.VisualTest.ImageComparisonUtil;
import VisualTest.VisualTest.ImageMultipleComparisonUtil;
import VisualTest.VisualTest.OcrActualExcelWriter;
import VisualTest.VisualTest.OcrBaselineExcelWriter;
import VisualTest.VisualTest.OcrFullPageScreenshotUtil;
import VisualTest.VisualTest.OcrTextComparisonUtil;
import VisualTest.VisualTest.OcrTextExtractor;
import VisualTest.VisualTest.ResponsiveCDPUtil;
import VisualTest.VisualTest.VisionAIUtil;
import VisualTest.VisualTest.VisualAnomalyDetector;
import VisualTest.VisualTest.VisualComparisonBatchUtil;
import dev.failsafe.internal.util.Assert;
import org.testng.asserts.*;
import org.testng.*;


public class TestCase_04 {
	
	// Take Baseline DOM Text and Save in Excel:
	@Test
	
	public void TestDomBaselineExtractor () throws InterruptedException {
	    WebDriver driver = new ChromeDriver();
	    driver.get("https://practice.expandtesting.com/tables");
	    driver.manage().window().maximize();
	    Thread.sleep(3000);
	    
	    //to perform scroll on an application using Selenium

	    JavascriptExecutor js = (JavascriptExecutor) driver;
	    js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
	    
	    
	    Thread.sleep(3000);
	    
		
	    // Extract DOM text
        DomTextExtractor extractor = new DomTextExtractor(driver);
        String domText = extractor.extractAllDomText();

        // Print extracted text
        System.out.println("Extracted DOM Text:\n" + domText);
        
        // Update your baseline runner to call this after extracting DOM text:
        String domText1 = new DomTextExtractor(driver).extractAllDomText();
        DomTextExcelWriter.saveDomTextToExcel(domText1);

        driver.quit();
	}
	
	// Take Actual DOM Text and Save in Excel:
		@Test
		
		public void TestDomActualExtractor () throws InterruptedException {
		    WebDriver driver = new ChromeDriver();
		    driver.get("https://practice.expandtesting.com/tables");
		    driver.manage().window().maximize();
		    Thread.sleep(3000);
		    
		    //to perform scroll on an application using Selenium

		    JavascriptExecutor js = (JavascriptExecutor) driver;
		    js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
		    
		    
		    Thread.sleep(3000);
		    
			
		    // Extract DOM text
	        DomTextExtractor extractor = new DomTextExtractor(driver);
	        String domText = extractor.extractAllDomText();
	        
	        // Update your baseline runner to call this after extracting DOM text:
	        String actualDomText = new DomTextExtractor(driver).extractAllDomText();
	        DomActualExcelWriter.saveDomTextToExcel(actualDomText);

	        driver.quit();
		}
		
		// Baseline vs Actual Comparison: 
		
		@Test
		public void DOMTextComparator() throws Exception {
		
			DomTextComparator.compareDomTextExcelFiles();
		}

		// Take full page - SS for OCR - Text Generation:
		@Test
		public void OcrFullPageScreenshot() throws Exception {
			WebDriver driver = new ChromeDriver();
		    driver.get("https://practice.expandtesting.com/tables");
		    driver.manage().window().maximize();
		    Thread.sleep(3000);
		OcrFullPageScreenshotUtil.captureFullPageScreenshot(driver);
		
		driver.quit();
		}
		
		// OCR Text Extraction 
		
		// Baseline: 
		
		@Test
		public void OCRBaselineTextExtractor() throws Exception {
		
			OcrTextExtractor ocr = new OcrTextExtractor ("C:\\Program Files (x86)\\Tesseract-OCR\\tessdata");
			String latestOcrText = ocr.extractTextFromLatestScreenshot(); // Automatically uses latest image
			
			System.out.println("🧠 OCR Text:\n" + latestOcrText);
			
			OcrBaselineExcelWriter.writeToExcel(latestOcrText); 
		}
		
		// Actual: 
		
		@Test
		public void OCRActualTextExtractor() throws Exception {
		
			OcrTextExtractor ocr = new OcrTextExtractor ("C:\\Program Files (x86)\\Tesseract-OCR\\tessdata");
			String latestOcrText = ocr.extractTextFromLatestScreenshot(); // Automatically uses latest image
			
			System.out.println("🧠 OCR Text:\n" + latestOcrText);
			
			OcrActualExcelWriter.writeToExcel(latestOcrText); 
		}
		
				// Baseline vs Actual Comparison: 
		
				@Test
				public void OCRTextComparator() throws Exception {
				
					OcrTextComparisonUtil.compareLatestOCRTextExcels();
				}

				
				// HybridDomOcrComparison: 
				
				@Test
				public void HybridReportGeneration() throws Exception {
				
					HybridDomOcrComparisonUtil.generateHybridTextValidationReport();

				}
				
				// JSON Converter: 
				
				@Test
				public void exportHybridTextReportToJson () throws Exception {
				
					HybridTextJsonExporter.exportHybridTextReportToJson("https://soffront.com/");

				}
				
				// Mistral AI-Based Analysis:
				// a. Confidence Score: b. Sentiment Analysis c. Content Quality Score:
				
				@Test
				public void MistralScoreAnalysis () throws Exception {
				String latestJson = "src\\main\\resources\\HybridTextValidationResult\\Hybrid_Report_20250701_002953.json";
				HuggingFaceScorerUtil.callMistralOnHuggingFace(latestJson);
				}
				
				
}
