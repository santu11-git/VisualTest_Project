package VisualTest.VisualTest;

import java.io.File;

public class FailSafeUtility {

    public static boolean isFolderContainsImage(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("⚠ Folder does not exist: " + folderPath);
            return false;
        }

        File[] imageFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("⚠ No images found in folder: " + folderPath);
            return false;
        }

        return true;
    }

    public static boolean validateScreenshotAvailability(String baselineFolder, String actualFolder) {
        boolean baselineExists = isFolderContainsImage(baselineFolder);
        boolean actualExists = isFolderContainsImage(actualFolder);

        if (!baselineExists && !actualExists) {
            System.out.println("❌ Both Baseline and Actual folders are empty.");
            return false;
        } else if (!baselineExists) {
            System.out.println("❌ Baseline images not available.");
            return false;
        } else if (!actualExists) {
            System.out.println("❌ Actual images not available.");
            return false;
        }

        return true;
    }
    
    public static void createDirectoryIfNotExists(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("📂 Directory created: " + directoryPath);
            } else {
                System.out.println("❌ Failed to create directory: " + directoryPath);
            }
        } else {
            System.out.println("✅ Directory already exists: " + directoryPath);
        }
    }

}
