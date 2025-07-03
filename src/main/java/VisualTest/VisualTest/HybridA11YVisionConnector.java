package VisualTest.VisualTest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class HybridA11YVisionConnector {

    private static final String OUTPUT_PATH = "src/main/resources/output/";

    public static void processA11YScreenshotsWithVisionAI() {
        try {
            // Ensure base output folder exists
            File baseFolder = new File(OUTPUT_PATH);
            if (!baseFolder.exists()) {
                System.out.println("⚠ Output folder does not exist: " + OUTPUT_PATH);
                return;
            }

            // Filter for A11Y folders
            File[] violationFolders = baseFolder.listFiles(file ->
                    file.isDirectory() && file.getName().startsWith("A11Y_Violations_")
            );

            if (violationFolders == null || violationFolders.length == 0) {
                System.out.println("⚠ No A11Y violation folders found under: " + OUTPUT_PATH);
                return;
            }

            // Sort to get latest folder
            Arrays.sort(violationFolders, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            File latestFolder = violationFolders[0];

            String timestamp = extractTimestampFromFolderName(latestFolder.getName());
            String folderPath = latestFolder.getAbsolutePath();
            System.out.println("✅ Processing Vision AI for folder: " + folderPath);

            File[] files = latestFolder.listFiles((dir, name) -> name.endsWith(".png"));
            if (files == null || files.length == 0) {
                System.out.println("⚠ No screenshots found for Vision AI processing.");
                return;
            }

            // Process each image with VisionAI
            for (File imageFile : files) {
                VisionAIUtil.analyzeImage(imageFile, latestFolder); // You already have this implemented
            }

            // Final Excel summary
            GenAIA11YReportGenerator.generateVisionAIReport();

        } catch (Exception e) {
            System.out.println("❌ Vision AI processing failed: " + e.getMessage());
        }
    }

    private static String extractTimestampFromFolderName(String folderName) {
        // A11Y_Violations_20250629_151927 → extract 20250629_151927
        return folderName.substring(folderName.lastIndexOf("_") - 8);
    }
}
