package VisualTest.VisualTest;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;

public class VisualTest03_Specific_ElementforBaseline {

    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com/v1/"); // Change to your target URL
        
        // Find the element you want to capture
       // WebElement loginButton = driver.findElement(By.xpath("//input[@id='login-button']")); // Replace with your element's locator

        WebDriverWait wait = new WebDriverWait (driver, Duration.ofSeconds(10));
        WebElement loginButton = wait.until((ExpectedConditions.visibilityOfElementLocated(By.id("user-name"))));
        
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", loginButton);
        // Take a screenshot of the specific element
        
        Screenshot screenshot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .shootingStrategy(ShootingStrategies.viewportPasting(10))
                .takeScreenshot(driver, loginButton);

        BufferedImage image = screenshot.getImage();

        // Save the element screenshot as baseline
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
    	
        driver.quit();
    }
}


