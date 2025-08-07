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

    public static String captureFullPageScreenshotBaseline(WebDriver driver) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Get full page height
        Long scrollHeight = (Long) js.executeScript("return document.body.scrollHeight");
        Long viewportHeight = (Long) js.executeScript("return window.innerHeight");

        List<BufferedImage> capturedImages = new ArrayList<>();
        int yOffset = 0;
        int scrollBuffer = 50; // Overlap buffer to reduce stitching glitches

        // Capture screenshots by scrolling
        while (yOffset < scrollHeight) {
            js.executeScript("window.scrollTo(0, arguments[0]);", yOffset);
            Thread.sleep(800); // Let the page render

            Screenshot screenshot = new AShot().takeScreenshot(driver);
            BufferedImage image = screenshot.getImage();
            capturedImages.add(image);

            int actualCapturedHeight = image.getHeight();
            yOffset += (actualCapturedHeight - scrollBuffer);
        }

        // Stitch captured screenshots
        int finalWidth = capturedImages.get(0).getWidth();
        int finalHeight = capturedImages.stream().mapToInt(BufferedImage::getHeight).sum();

        BufferedImage finalImage = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = finalImage.getGraphics();

        int currentHeight = 0;
        for (BufferedImage img : capturedImages) {
            graphics.drawImage(img, 0, currentHeight, null);
            currentHeight += img.getHeight();
        }
        graphics.dispose();

        // Save in machine-independent temp directory under a timestamped folder
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String tempDir = System.getProperty("java.io.tmpdir");
        String folderPath = tempDir + File.separator + "VisualIQ" + File.separator + "BaselineSS" + File.separator + timestamp;

        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        File output = new File(folder, "FullPageSS.png");
        ImageIO.write(finalImage, "png", output);

        System.out.println("✅ Baseline full page screenshot saved at: " + output.getAbsolutePath());

        // Return folder path for tracking
        return folderPath;
    }


    // For Multiple Screenshots:
    
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


