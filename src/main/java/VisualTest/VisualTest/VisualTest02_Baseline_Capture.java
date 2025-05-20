package VisualTest.VisualTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class VisualTest02_Baseline_Capture {

	    public static void main(String[] args) throws Exception {
	        // Set up the Chrome driver
	        WebDriver driver = new ChromeDriver();

	        // Navigate to the page you want to capture
	        driver.get("https://www.saucedemo.com/v1/"); // Change this URL as needed

	        // Take a full-page screenshot using AShot
	        Screenshot screenshot = new AShot().takeScreenshot(driver);
	        BufferedImage image = screenshot.getImage();

	        // Save the screenshot as the baseline
	        String folderPath="src/main/resources/BaselineSS";
	        File folder = new File (folderPath);
	        if (!folder.exists()) {
	        	folder.mkdirs();
	        }
	        VisualTest04_TimeStamp TS =new VisualTest04_TimeStamp();
	        String fileName= TS.TimeStamp_Logic();
	        File outputfile = new File(folderPath + "/" + fileName);
	        ImageIO.write(image, "png", outputfile);

	        System.out.println("Baseline screenshot saved at: " + outputfile.getAbsolutePath());

	        driver.close();
	    }
	}


