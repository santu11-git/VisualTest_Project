package VisualTest.VisualTest;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

public class VisualTest07_MultipleElementCapture {
    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.way2automation.com/lifetime-membership-club/");
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Create folder for baseline screenshots
        String folderPath = "src/main/resources/BaselineSS";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        // Capture username field
        captureElementScreenshot(driver, wait, By.xpath("(//h2[@class='elementor-heading-title elementor-size-default'])[1]"), "UsernameField", folderPath);

        // Capture password field
        captureElementScreenshot(driver, wait, By.xpath("(//h2[@class='elementor-heading-title elementor-size-default'])[2]"), "PasswordField", folderPath);

        // Capture login button
        captureElementScreenshot(driver, wait, By.xpath("(//img[@class='lazyloaded'])[1]"), "LoginButton", folderPath);

        // Capture login logo
        captureElementScreenshot(driver, wait, By.xpath("//img[@class='attachment-full size-full wp-image-26494 lazyloaded']"), "LoginLogo", folderPath);

        // Capture robot image
        captureElementScreenshot(driver, wait, By.xpath("//h3[@class='elementor-heading-title elementor-size-large']"), "BotImage", folderPath);

        driver.quit();
    }

    private static void captureElementScreenshot(WebDriver driver, WebDriverWait wait, By locator, String elementName, String folderPath) throws Exception {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500); // Allow scroll to complete

        File src = element.getScreenshotAs(OutputType.FILE);
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File dest = new File(folderPath + "/" + elementName + "_" + timestamp + ".png");
        src.getAbsoluteFile();
        FileUtils.copyFile(src, dest);
        System.out.println("Saved: " + dest.getAbsolutePath());
    }
}


