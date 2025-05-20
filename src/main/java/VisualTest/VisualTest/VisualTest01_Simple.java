package VisualTest.VisualTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class VisualTest01_Simple {

    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://example.com");

        // Take a screenshot -
        Screenshot screenshot = new AShot().takeScreenshot(driver);
        BufferedImage actualImage = screenshot.getImage();

        // Save new image (optional) -
        ImageIO.write(actualImage, "PNG", new File("screenshots/actual.png"));

        // Load baseline image -
        BufferedImage expectedImage = ImageIO.read(new File("screenshots/baseline.png"));

        // Compare -
        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(expectedImage, actualImage);

        if (diff.hasDiff()) {
            System.out.println("UI has changed!");
            ImageIO.write(diff.getMarkedImage(), "PNG", new File("screenshots/diff.png"));
        } else {
            System.out.println("UI matches the baseline.");
        }

        driver.quit();
    }
}