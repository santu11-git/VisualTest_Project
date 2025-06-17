package VisualTest.VisualTest;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

//This is the final logic - Combination of Robot Class + AShot library: 
public class ActualScreenshot {

	 public static void captureFullPageScreenshotActual(WebDriver driver, String folderName) throws Exception {
	        JavascriptExecutor js = (JavascriptExecutor) driver;
	        Long totalHeight = (Long) js.executeScript("return document.body.scrollHeight");

	        Long viewportHeight = (Long) js.executeScript("return window.innerHeight");

	        List<BufferedImage> capturedImages = new ArrayList<>();
	        int yOffset = 0;

	        while (yOffset < totalHeight) {
	            js.executeScript("window.scrollTo(0, arguments[0])", yOffset);
	            Thread.sleep(800);

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

	        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String folderPath = "src/main/resources/ActualSS";
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
	    
	    public static void captureMultipleScreenshotsActual(List<String> urls, WebDriver driver, String mode) throws Exception {
	        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String folderName = mode.equalsIgnoreCase("baseline")
	                ? "BaselineSS/" + timestamp + "_Baseline"
	                : "ActualSS/" + timestamp + "_Actual";

	        File folder = new File("src/main/resources/" + folderName);
	        folder.mkdirs();

	        JavascriptExecutor js = (JavascriptExecutor) driver;

	        for (int i = 0; i < urls.size(); i++) {
	            driver.get(urls.get(i));
	            Thread.sleep(1000);

	            Long totalHeight = (Long) js.executeScript("return document.body.scrollHeight");
	            Long viewportHeight = (Long) js.executeScript("return window.innerHeight");

	            List<BufferedImage> images = new ArrayList<>();
	            int yOffset = 0;

	            while (yOffset < totalHeight) {
	                js.executeScript("window.scrollTo(0, arguments[0])", yOffset);
	                Thread.sleep(800);

	                Screenshot screenshot = new AShot().takeScreenshot(driver);
	                images.add(screenshot.getImage());
	                yOffset += viewportHeight;
	            }

	            int width = images.get(0).getWidth();
	            int height = images.stream().mapToInt(BufferedImage::getHeight).sum();
	            BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	            int y = 0;
	            for (BufferedImage image : images) {
	                finalImage.getGraphics().drawImage(image, 0, y, null);
	                y += image.getHeight();
	            }

	            File output = new File(folder, "Image_" + String.format("%02d", i + 1) + ".png");
	            ImageIO.write(finalImage, "png", output);
	        }

	        System.out.println("✅ All screenshots saved at: " + folder.getAbsolutePath());
	    }
}
