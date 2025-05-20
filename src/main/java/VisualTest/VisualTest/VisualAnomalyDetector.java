package VisualTest.VisualTest;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class VisualAnomalyDetector {

    // Take screenshot and send to GPT Vision
	public static void detectImageVisualAnomalies(WebDriver driver) throws InterruptedException {
        try {
            // ✅ Step 1: Wait 15 seconds to avoid hitting rate limit (after text check)
            System.out.println("Staggering request: waiting 15 seconds before visual anomaly check...");
            Thread.sleep(15000);

            // ✅ Step 2: Take full-page screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage originalImage = ImageIO.read(screenshot);

            // ✅ Step 3: Resize screenshot to 800x600
            int newWidth = 800;
            int newHeight = 600;
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
            g.dispose();

            // ✅ Step 4: Save resized screenshot to target folder
            String folderPath = "src/main/resources/screenshots";
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            File outputFile = new File(folder, "live_screenshot_resized.png");
            ImageIO.write(resizedImage, "png", outputFile);

            // ✅ Step 5: Send to GPT Vision
            ChatGPTClient client = new ChatGPTClient();
            String prompt = "You are a UI/UX expert. Analyze this webpage screenshot and report any visual anomalies like alignment issues, color inconsistencies, spacing problems, or visual incoherence.";
            String result = client.detectImageAnomalies(outputFile, prompt);

            // ✅ Step 6: Print output
            System.out.println("=== GPT Visual Anomaly (Image) Report ===");
            System.out.println(result);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Failed during image processing: " + e.getMessage());
        }
    }


    // Extract CSS and send to GPT for anomaly analysis
	public static void detectTextualVisualAnomalies(WebElement... elements) {
	    if (elements == null || elements.length == 0) {
	        System.out.println("No elements provided for textual analysis.");
	        return;
	    }

	    StringBuilder promptBuilder = new StringBuilder();
	    promptBuilder.append("You are an expert UI/UX reviewer. ")
	                 .append("Analyze the following UI components for any design or visual inconsistencies or accessibility concerns. ")
	                 .append("Evaluate alignment, font usage, size, spacing, color consistency, label clarity, and other UI/UX issues. ")
	                 .append("For any anomalies found, suggest specific improvements or best practices to fix them.\n\n");

	    for (WebElement element : elements) {
	        try {
	            String tag = element.getTagName();
	            String id = element.getAttribute("id");
	            String name = element.getAttribute("name");
	            String type = element.getAttribute("type");
	            String text = element.getText().trim();
	            String classAttr = element.getAttribute("class");
	            String style = element.getAttribute("style");
	            Dimension size = element.getSize();
	            Point location = element.getLocation();
	            String label = (id != null && !id.isEmpty()) ? "ID: " + id :
	                           (name != null && !name.isEmpty()) ? "Name: " + name :
	                           "Tag: " + tag;

	            promptBuilder.append("Element: ").append(label).append("\n")
	                         .append(" - Tag: ").append(tag).append("\n")
	                         .append(" - Type: ").append(type).append("\n")
	                         .append(" - Text: ").append(text).append("\n")
	                         .append(" - Class: ").append(classAttr).append("\n")
	                         .append(" - Style: ").append(style).append("\n")
	                         .append(" - Size: ").append(size.getWidth()).append("x").append(size.getHeight()).append("\n")
	                         .append(" - Position: (").append(location.getX()).append(", ").append(location.getY()).append(")\n\n");
	        } catch (Exception e) {
	            promptBuilder.append("Failed to extract properties for one element: ").append(e.getMessage()).append("\n\n");
	        }
	    }

	    // Call GPT with the constructed prompt
	    ChatGPTClient client = new ChatGPTClient();
	    String result = client.detectTextAnomalies(promptBuilder.toString());
	    System.out.println("=== GPT Visual Anomaly (Textual) Report with Suggestions ===\n" + result);
	}
}