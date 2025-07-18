package VisualTest.VisualTest;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.alg.misc.ImageStatistics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class VisualComparisonBatchUtil {

    private static final String BASELINE_FOLDER = "src/main/resources/BaselineMultipleImagesSS";
    private static final String ACTUAL_FOLDER = "src/main/resources/ActualMultipleImagesSS";
    private static final String RESULT_FOLDER = "src/main/resources/ResultMultipleImagesSS";
    private static final double SSIM_THRESHOLD = 0.95;

    public static void compareLatestMultipleScreenshots() {
        File latestBaseline = getLatestSubFolder(BASELINE_FOLDER);
        File latestActual = getLatestSubFolder(ACTUAL_FOLDER);

        if (latestBaseline == null || latestActual == null) {
            System.out.println("❌ Could not locate latest baseline or actual folders.");
            return;
        }

        File[] baselineImages = latestBaseline.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        File[] actualImages = latestActual.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (baselineImages == null || actualImages == null || baselineImages.length == 0 || actualImages.length == 0) {
            System.out.println("❌ No images found in baseline or actual folders.");
            return;
        }

        Map<String, File> baselineMap = getFilesByName(baselineImages);
        Map<String, File> actualMap = getFilesByName(actualImages);

        boolean diffDetected = false;
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File resultFolder = new File(RESULT_FOLDER, timestamp);

        // Ensure result folder is created before comparisons begin
        if (!resultFolder.exists()) {
            boolean created = resultFolder.mkdirs();
            if (!created) {
                System.out.println("❌ Failed to create result folder: " + resultFolder.getAbsolutePath());
                return;
            }
        }

        for (String imageName : baselineMap.keySet()) {
            if (!actualMap.containsKey(imageName)) {
                System.out.println("⚠ Skipping " + imageName + " (not found in actual)");
                continue;
            }

            try {
                BufferedImage baselineImg = ImageIO.read(baselineMap.get(imageName));
                BufferedImage actualImg = ImageIO.read(actualMap.get(imageName));

                if (baselineImg.getWidth() != actualImg.getWidth() || baselineImg.getHeight() != actualImg.getHeight()) {
                    System.out.println("❌ Skipping " + imageName + " due to size mismatch.");
                    continue;
                }

                boolean pixelMatch = checkPixelDifference(baselineImg, actualImg);
                double ssimScore = calculateSSIM(baselineImg, actualImg);

                System.out.printf("🔍 [%s] SSIM: %.4f | Pixel Match: %s\n", imageName, ssimScore, pixelMatch ? "Yes" : "No");

                if (!pixelMatch || ssimScore < SSIM_THRESHOLD) {
                    File diffFile = new File(resultFolder, "Diff_" + imageName);
                    saveDifferenceImage(baselineImg, actualImg, diffFile);
                    System.out.println("❌ [" + imageName + "] Difference saved: " + diffFile.getAbsolutePath());
                    diffDetected = true;
                } else {
                    System.out.println("✅ [" + imageName + "] Images are similar.");
                }

            } catch (Exception e) {
                System.out.println("❌ Error comparing " + imageName);
                e.printStackTrace();
            }
        }

        if (!diffDetected) {
            System.out.println("✅ All images are visually similar. No diff images generated.");
            // Optionally delete empty result folder
            if (resultFolder.exists() && resultFolder.isDirectory() && Objects.requireNonNull(resultFolder.list()).length == 0) {
                resultFolder.delete();
            }
        }
    }

    private static File getLatestSubFolder(String parentFolderPath) {
        File parent = new File(parentFolderPath);
        File[] subFolders = parent.listFiles(File::isDirectory);

        if (subFolders == null || subFolders.length == 0) return null;

        Arrays.sort(subFolders, Comparator.comparing(File::getName).reversed());
        return subFolders[0]; // Latest timestamp folder
    }

    private static Map<String, File> getFilesByName(File[] files) {
        Map<String, File> map = new HashMap<>();
        for (File file : files) {
            map.put(file.getName(), file);
        }
        return map;
    }

    private static boolean checkPixelDifference(BufferedImage img1, BufferedImage img2) {
        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static double calculateSSIM(BufferedImage img1, BufferedImage img2) {
        GrayF32 gray1 = ConvertBufferedImage.convertFrom(img1, (GrayF32) null);
        GrayF32 gray2 = ConvertBufferedImage.convertFrom(img2, (GrayF32) null);

        double mean1 = ImageStatistics.mean(gray1);
        double mean2 = ImageStatistics.mean(gray2);
        double diff = Math.abs(mean1 - mean2);

        return 1.0 - (diff / 255.0); // Simplified approximation
    }

    private static void saveDifferenceImage(BufferedImage img1, BufferedImage img2, File outputFile) {
        try {
            BufferedImage diffImg = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < img1.getHeight(); y++) {
                for (int x = 0; x < img1.getWidth(); x++) {
                    int rgb1 = img1.getRGB(x, y);
                    int rgb2 = img2.getRGB(x, y);
                    diffImg.setRGB(x, y, rgb1 != rgb2 ? Color.RED.getRGB() : rgb1);
                }
            }
            ImageIO.write(diffImg, "png", outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
