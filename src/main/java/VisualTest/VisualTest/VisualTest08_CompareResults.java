package VisualTest.VisualTest;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import ru.yandex.qatools.ashot.*;
import ru.yandex.qatools.ashot.comparison.*;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class VisualTest08_CompareResults {

    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.saucedemo.com/v1/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Parallel arrays:
        String[] elementNames = {
                "UsernameField",
                "PasswordField",
                "LoginButton",
                "LoginLogo",
                "BotImage"
        };

        By[] locators = {
                By.id("user-name"),
                By.id("password"),
                By.id("login-button"),
                By.className("login_logo"),
                By.className("bot_column")
        };

        String baselinePath = "src/main/resources/BaselineSS";
        String actualPath = "src/main/resources/ActualSS";
        String resultPath = "src/main/resources/ResultSS";

        createFolder(baselinePath);
        createFolder(actualPath);
        createFolder(resultPath);

        for (int i = 0; i < elementNames.length; i++) {
            compareElement(driver, wait, locators[i], elementNames[i], baselinePath, actualPath, resultPath);
        }

        driver.quit();
    }

    private static void compareElement(WebDriver driver, WebDriverWait wait, By locator, String elementName,
                                       String baselinePath, String actualPath, String resultPath) throws Exception {

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);

        // Save actual screenshot using getScreenshotAs
        File actualFile = element.getScreenshotAs(OutputType.FILE);
        actualFile.getAbsoluteFile();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File actualDest = new File(actualPath + "/" + elementName + "_Actual_" + timestamp + ".png");
        copyFile(actualFile, actualDest);

        // Load baseline image
        File baselineFile = findLatestBaseline(baselinePath, elementName);
        if (baselineFile == null) {
            System.out.println("No baseline found for: " + elementName);
            return;
        }

        BufferedImage baselineImage = ImageIO.read(baselineFile);

        // Take actual image using AShot
		/*
		 * Screenshot actualShot = new
		 * AShot().shootingStrategy(ShootingStrategies.viewportPasting(100))
		 * .takeScreenshot(driver, element);
		 */
        
        Screenshot actualShot = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(driver, element);
        BufferedImage actualImageForCompare = actualShot.getImage();

        // Compare images
        ImageDiffer imgDiff = new ImageDiffer();
        ImageDiff diff = imgDiff.makeDiff(baselineImage, actualImageForCompare);

        List<String> diffPaths = new ArrayList<>();

     // Inside your loop
     if (diff.hasDiff()==true) {
         File diffFile = new File(resultPath + "/" + elementName + "_Diff_" + timestamp + ".png");
         ImageIO.write(diff.getMarkedImage(), "png", diffFile);
         diffPaths.add(diffFile.getAbsolutePath());
         System.out.println("Mismatch found for: " + elementName);
     } else {
         System.out.println("Match found for: " + elementName);
     }

     // After all comparisons
     VisualTest09_ReportGenerator.generateHTMLReport(diffPaths, resultPath);
    }

    private static File findLatestBaseline(String folderPath, String elementName) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.contains(elementName));
        if (files == null || files.length == 0) return null;

        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0];
    }

    private static void createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) folder.mkdirs();
    }

    private static void copyFile(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}
