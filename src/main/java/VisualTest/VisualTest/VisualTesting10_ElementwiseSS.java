package VisualTest.VisualTest;

import org.openqa.selenium.*;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class VisualTesting10_ElementwiseSS {

    public static void captureElementScreenshot(WebDriver driver, WebElement element) throws Exception {
        // Use AShot to capture full-page element screenshot
        Screenshot screenshot = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .shootingStrategy(ShootingStrategies.viewportPasting(100)) // Scrolls and stitches
                .takeScreenshot(driver, element);

        // Save to folder with timestamp
        String folderPath = "src/main/resources/BaselineSS";
        File folder = new File(folderPath);
        if (!folder.exists()) folder.mkdirs();

        VisualTest04_TimeStamp TS = new VisualTest04_TimeStamp();
        String fileName = TS.TimeStamp_Logic();
        File outputfile = new File(folderPath + "/" + fileName);

        BufferedImage image = screenshot.getImage();
        ImageIO.write(image, "png", outputfile);

        System.out.println("AShot screenshot saved at: " + outputfile.getAbsolutePath());
    }
}
