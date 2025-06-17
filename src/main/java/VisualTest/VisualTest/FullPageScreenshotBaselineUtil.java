package VisualTest.VisualTest;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// This is the final logic - Combination of JS Class + AShot library: 
public class FullPageScreenshotBaselineUtil {

    public static void captureFullPageScreenshotBaseline(WebDriver driver, String folderName) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        Long totalHeight = (Long) js.executeScript("return document.body.scrollHeight");
        Long viewportHeight = (Long) js.executeScript("return window.innerHeight");

        List<BufferedImage> capturedImages = new ArrayList<>();
        int yOffset = 0;
        int scrollBuffer = 100; // Overlap buffer to reduce stitching artifacts

        while (yOffset < totalHeight) {
            // 🟡 Smooth scroll with slight overlap
            js.executeScript("window.scrollTo(0, arguments[0])", yOffset);
            Thread.sleep(1000); // Allow rendering and scroll sync

            // 🟡 Optional: wait for DOM to stabilize/lazy load
            js.executeScript("return new Promise(resolve => setTimeout(resolve, 300));");

            Screenshot screenshot = new AShot().takeScreenshot(driver);
            capturedImages.add(screenshot.getImage());

            yOffset += viewportHeight - scrollBuffer;
        }

        // ✅ Combine captured images vertically
        int fullImageWidth = capturedImages.get(0).getWidth();
        int fullImageHeight = capturedImages.stream().mapToInt(BufferedImage::getHeight).sum();

        BufferedImage finalImage = new BufferedImage(fullImageWidth, fullImageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = finalImage.getGraphics();
        int currentHeight = 0;

        for (BufferedImage image : capturedImages) {
            graphics.drawImage(image, 0, currentHeight, null);
            currentHeight += image.getHeight();
        }

        graphics.dispose(); // 🧹 good practice

        // ✅ Save stitched screenshot with timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String folderPath = "src/main/resources/BaselineSS";

        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        File output = new File(folderPath + "/FullPageSS_" + timestamp + ".png");
        ImageIO.write(finalImage, "png", output);

        System.out.println("✅ Full stitched screenshot saved at: " + output.getAbsolutePath());
    }


    public static WebDriver getChromeDriver() {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        return driver;
    }
    
    // For Multiple Screenshots:
    
 // Single page capture (QA will only call this)
    public static void captureScreenshotForMultipleCurrentPageBaseline(WebDriver driver, String mode, String pageName) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Long totalHeight = (Long) js.executeScript("return document.body.scrollHeight");
        Long viewportHeight = (Long) js.executeScript("return window.innerHeight");

        List<BufferedImage> capturedImages = new ArrayList<>();
        int yOffset = 0;

        while (yOffset < totalHeight) {
            js.executeScript("window.scrollTo(0, arguments[0])", yOffset);
            Thread.sleep(800);  // You may optimize this delay
            Screenshot screenshot = new AShot().takeScreenshot(driver);
            capturedImages.add(screenshot.getImage());
            yOffset += viewportHeight;
        }

        int fullImageWidth = capturedImages.get(0).getWidth();
        int fullImageHeight = capturedImages.stream().mapToInt(BufferedImage::getHeight).sum();
        BufferedImage finalImage = new BufferedImage(fullImageWidth, fullImageHeight, BufferedImage.TYPE_INT_RGB);

        int currentHeight = 0;
        for (BufferedImage image : capturedImages) {
            finalImage.getGraphics().drawImage(image, 0, currentHeight, null);
            currentHeight += image.getHeight();
        }

        // ✅ Use shared timestamp
        String timestamp = ScreenshotCaptureSessionTracker.getTimestamp();
        String folderName = mode.equalsIgnoreCase("actual") ? "ActualMultipleImagesSS" : "BaselineMultipleImagesSS";
        String fullDirPath = "src/main/resources/" + folderName + "/" + timestamp;

        File outputDir = new File(fullDirPath);
        if (!outputDir.exists()) outputDir.mkdirs();

        File outputFile = new File(outputDir, pageName + ".png");
        ImageIO.write(finalImage, "png", outputFile);

        System.out.println("✅ Screenshot for " + pageName + " saved at: " + outputFile.getAbsolutePath());
    }

}


