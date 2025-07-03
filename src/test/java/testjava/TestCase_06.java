package testjava;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import VisualTest.VisualTest.AITCGherkinTestCaseGenerator;

public class TestCase_06 {
	
	// Take Baseline DOM Text and Save in Excel:
		@Test
		
		public void TestDomBaselineExtractor () throws InterruptedException {
			AITCGherkinTestCaseGenerator.extractGherkinText("https://opensource-demo.orangehrmlive.com/web/index.php/auth/login");  
		
			}

}
