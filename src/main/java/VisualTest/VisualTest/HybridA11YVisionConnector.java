package VisualTest.VisualTest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HybridA11YVisionConnector {

    public static void processA11YScreenshotsWithVisionAI(String basePath) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File a11yFolder = new File(basePath);
            File[] violationFolders = a11yFolder.listFiles(File::isDirectory);

            if (violationFolders == null || violationFolders.length == 0) {
                System.out.println("⚠ No A11Y violation folders found.");
                return;
            }

            File latestFolder = violationFolders[violationFolders.length - 1];
            String folderPath = latestFolder.getAbsolutePath();
            timestamp = extractTimestampFromFolderName(latestFolder.getName());

            System.out.println("✅ Processing Vision AI for: " + folderPath);

            // Process Vision AI for each screenshot
            File[] files = latestFolder.listFiles((dir, name) -> name.endsWith(".png"));
            if (files == null || files.length == 0) {
                System.out.println("⚠ No screenshots found for Vision AI processing.");
                return;
            }

            for (File imageFile : files) {
                VisionAIUtil.analyzeImage(imageFile, latestFolder);
            }

            // Once JSON generation done, automatically create Excel report too
            GenAIA11YReportGenerator.generateVisionAIReport();

        } catch (Exception e) {
            System.out.println("❌ Vision AI processing failed: " + e.getMessage());
        }
    }

    private static String extractTimestampFromFolderName(String folderName) {
        // assuming folderName = A11Y_Violations_yyyyMMdd_HHmmss
        return folderName.substring(folderName.lastIndexOf("_") - 8);
    }
}
