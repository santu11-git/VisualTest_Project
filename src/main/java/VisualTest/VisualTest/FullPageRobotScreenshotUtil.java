package VisualTest.VisualTest;


import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FullPageRobotScreenshotUtil {

    public static void captureFullPageScreenshot(WebDriver driver, String folderName) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("window.scrollTo(0, 0);");
        Thread.sleep(1000);

        Long totalHeight = (Long) js.executeScript("return document.body.scrollHeight");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        Robot robot = new Robot();
        List<BufferedImage> screenshots = new ArrayList<>();

        int scrollY = 0;
        while (scrollY < totalHeight) {
            js.executeScript("window.scrollTo(0, arguments[0]);", scrollY);
            Thread.sleep(1000);

            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(screenWidth, screenHeight));
            screenshots.add(screenshot);

            scrollY += screenHeight;
        }

        int stitchedHeight = screenshots.size() * screenHeight;
        BufferedImage finalImage = new BufferedImage(screenWidth, stitchedHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = finalImage.createGraphics();

        int currentY = 0;
        for (BufferedImage img : screenshots) {
            g2d.drawImage(img, 0, currentY, null);
            currentY += img.getHeight();
        }
        g2d.dispose();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String folderPath = "src/main/resources/BaselineSS";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        File output = new File(folderPath + "/FullPageSS_" + timestamp + ".png");
        ImageIO.write(finalImage, "png", output);

        System.out.println("✅ Full stitched screenshot saved at: " + output.getAbsolutePath());
    }
}
