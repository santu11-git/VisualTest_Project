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

public class VisualTest05_ManulimageCrop {
	
    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com/v1/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement loginButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));

        // Scroll into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loginButton);
        Thread.sleep(500); // Allow scroll to complete

        // Take full screenshot
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImg = ImageIO.read(screenshotFile);

        // Get element location and size
        Point point = loginButton.getLocation();
        int eleWidth = loginButton.getSize().getWidth();
        int eleHeight = loginButton.getSize().getHeight();

        // Crop the element from full screenshot
        BufferedImage eleScreenshot = fullImg.getSubimage(point.getX(), point.getY(), eleWidth, eleHeight);

        // Generate timestamped filename
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String folderPath = "src/main/resources/BaselineSS";
        new File(folderPath).mkdirs();
        File outputfile = new File(folderPath + "/Baseline_" + timestamp + ".png");

        ImageIO.write(eleScreenshot, "png", outputfile);
        System.out.println("Baseline screenshot saved at: " + outputfile.getAbsolutePath());

        driver.quit();
    }
}

