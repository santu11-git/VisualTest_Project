package VisualTest.VisualTest;


import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.io.File;

import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.openqa.selenium.By;

import org.openqa.selenium.OutputType;

import org.openqa.selenium.TakesScreenshot;

public class VisualTest06_ElementScreenshots_Baseline {
	
	public static void main(String[] args) throws Exception {
		
		String ButtonXpatExpresion="//button[@id='login-button']";
		
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.way2automation.com/lifetime-membership-club/");
        driver.manage().window().maximize();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//h2[@class='elementor-heading-title elementor-size-default'])[2]")));

        // Scroll into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loginButton);
        Thread.sleep(500); // Allow scroll to complete
        
        //TakesScreenshot scrShot = ((TakesScreenshot)loginButton);
        File srcFile =   loginButton.getScreenshotAs(OutputType.FILE);
        
        String folderPath="src/main/resources/BaselineSS";
        File folder = new File (folderPath);
        if (!folder.exists()) { 
        	folder.mkdirs();
        }
        VisualTest04_TimeStamp TS =new VisualTest04_TimeStamp();
        String fileName= TS.TimeStamp_Logic();
      //  File srcFile =   scrShot.getScreenshotAs(OutputType.FILE);
        // File outputfile = new File(folderPath + "/" + fileName);
        FileUtils.copyFile(srcFile, new File(folderPath + "/" + fileName));
        System.out.println("Baseline screenshot saved ");
        Thread.sleep(100);
        driver.quit();
	}
}
