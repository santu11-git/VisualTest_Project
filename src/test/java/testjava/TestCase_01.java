package testjava;

import java.io.File;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

import VisualTest.VisualTest.ChatGPTClient;
import VisualTest.VisualTest.VisualAnomalyDetector;


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
	}


