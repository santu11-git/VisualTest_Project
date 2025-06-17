package VisualTest.VisualTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class VisionAIStub {

    public static void processMockVisionAI(File imageFile, File screenshotFolder) {
        try {
            // Simulated extracted text (You can modify this as you wish)
            String dummyExtractedText = "Sample extracted text from Vision AI Stub for: " + imageFile.getName();

            System.out.println("✅ [MOCK MODE] Extracted Text:");
            System.out.println(dummyExtractedText);

            // Save to dummy JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(Map.of("ExtractedText", dummyExtractedText));

            File jsonFile = new File(screenshotFolder, imageFile.getName().replace(".png", ".json"));
            try (FileOutputStream out = new FileOutputStream(jsonFile)) {
                out.write(jsonOutput.getBytes());
            }
            System.out.println("✅ [MOCK MODE] Vision result saved to: " + jsonFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("❌ [MOCK MODE] Exception: " + e.getMessage());
        }
    }
}
