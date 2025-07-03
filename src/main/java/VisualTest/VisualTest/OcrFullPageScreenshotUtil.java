package VisualTest.VisualTest;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OcrFullPageScreenshotUtil {

    public static String captureFullPageScreenshot(WebDriver driver) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        Long totalHeight = (Long) js.executeScript("return document.body.scrollHeight");
        Long viewportHeight = (Long) js.executeScript("return window.innerHeight");

        List<BufferedImage> capturedImages = new ArrayList<>();
        int yOffset = 0;
        int scrollBuffer = 100; // overlap to avoid stitching artifacts

        while (yOffset < totalHeight) {
            js.executeScript("window.scrollTo(0, arguments[0])", yOffset);
            Thread.sleep(1000); // give time for lazy load or scroll

            // Optionally wait for DOM to stabilize
            js.executeScript("return new Promise(resolve => setTimeout(resolve, 300));");

            Screenshot screenshot = new AShot().takeScreenshot(driver);
            capturedImages.add(screenshot.getImage());

            yOffset += viewportHeight - scrollBuffer;
        }

        // Stitch vertically
        int width = capturedImages.get(0).getWidth();
        int height = capturedImages.stream().mapToInt(BufferedImage::getHeight).sum();

        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = finalImage.getGraphics();
        int currentY = 0;

        for (BufferedImage image : capturedImages) {
            g.drawImage(image, 0, currentY, null);
            currentY += image.getHeight();
        }

        g.dispose();

        // Timestamped file name
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // OCR screenshot folder logic
        String folderPath = "src/main/resources/OCRTextSS";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        String fullPath = folderPath + "/ocrtext_" + timestamp + ".png";
        File output = new File(fullPath);
        ImageIO.write(finalImage, "png", output);

        System.out.println("✅ OCR Full Page Screenshot saved: " + output.getAbsolutePath());
        return fullPath;
    }
}
