package VisualTest.VisualTest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

public class ImageComparisonUtil {

    private static final String BASELINE_FOLDER = "src/main/resources/BaselineSS";
    private static final String ACTUAL_FOLDER = "src/main/resources/ActualSS";
    private static final String RESULT_FOLDER = "src/main/resources/ResultSS";

    // Helper: Find the latest image file in a folder
    private static File getLatestImageFile(File folder) {
        File[] imageFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (imageFiles == null || imageFiles.length == 0) return null;

        return Arrays.stream(imageFiles)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

    public static boolean compareLatestBaselineAndActual() {
        try {
            File baselineDir = new File(BASELINE_FOLDER);
            File actualDir = new File(ACTUAL_FOLDER);

            File baselineImage = getLatestImageFile(baselineDir);
            File actualImage = getLatestImageFile(actualDir);

            if (baselineImage == null || actualImage == null) {
                System.out.println("❌ Could not find latest screenshots in BaselineSS or ActualSS.");
                return false;
            }

            BufferedImage img1 = ImageIO.read(baselineImage);
            BufferedImage img2 = ImageIO.read(actualImage);

            if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
                System.out.println("❌ Image dimensions do not match.");
                return false;
            }

            boolean isSame = true;
            BufferedImage diffImage = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < img1.getHeight(); y++) {
                for (int x = 0; x < img1.getWidth(); x++) {
                    int rgb1 = img1.getRGB(x, y);
                    int rgb2 = img2.getRGB(x, y);

                    if (rgb1 != rgb2) {
                        diffImage.setRGB(x, y, Color.RED.getRGB());
                        isSame = false;
                    } else {
                        diffImage.setRGB(x, y, rgb1);
                    }
                }
            }

            if (!isSame) {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File resultFolder = new File(RESULT_FOLDER + "/" + timestamp);
                resultFolder.mkdirs();

                File diffFile = new File(resultFolder, "DiffResult_" + timestamp + ".png");
                ImageIO.write(diffImage, "png", diffFile);

                System.out.println("❌ Differences found. Saved at: " + diffFile.getAbsolutePath());
            } else {
                System.out.println("✅ No visual differences found.");
            }

            return isSame;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ New: Compare all matching baseline and actual images by file name (This is for batch page processing not for single page)
    public static boolean compareAllScreenshotsByName() {
        boolean allMatched = true;
        try {
            File baselineDir = new File(BASELINE_FOLDER);
            File actualDir = new File(ACTUAL_FOLDER);

            File[] baselineFiles = baselineDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            File[] actualFiles = actualDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

            if (baselineFiles == null || actualFiles == null) {
                System.out.println("❌ One or both folders are empty.");
                return false;
            }

            Map<String, File> baselineMap = Arrays.stream(baselineFiles)
                    .collect(Collectors.toMap(File::getName, file -> file));

            Map<String, File> actualMap = Arrays.stream(actualFiles)
                    .collect(Collectors.toMap(File::getName, file -> file));

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File resultFolder = new File(RESULT_FOLDER + "/" + timestamp);
            resultFolder.mkdirs();

            for (String fileName : baselineMap.keySet()) {
                File baselineImg = baselineMap.get(fileName);
                File actualImg = actualMap.get(fileName);

                if (actualImg == null) {
                    System.out.println("⚠️ No actual image found for: " + fileName);
                    allMatched = false;
                    continue;
                }

                BufferedImage img1 = ImageIO.read(baselineImg);
                BufferedImage img2 = ImageIO.read(actualImg);

                if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
                    System.out.println("❌ Size mismatch for: " + fileName);
                    allMatched = false;
                    continue;
                }

                boolean isSame = true;
                BufferedImage diffImage = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);

                for (int y = 0; y < img1.getHeight(); y++) {
                    for (int x = 0; x < img1.getWidth(); x++) {
                        int rgb1 = img1.getRGB(x, y);
                        int rgb2 = img2.getRGB(x, y);

                        if (rgb1 != rgb2) {
                            diffImage.setRGB(x, y, Color.RED.getRGB());
                            isSame = false;
                        } else {
                            diffImage.setRGB(x, y, rgb1);
                        }
                    }
                }

                if (!isSame) {
                    File diffFile = new File(resultFolder, "Diff_" + fileName);
                    ImageIO.write(diffImage, "png", diffFile);
                    System.out.println("❌ Difference in " + fileName + " → Saved at: " + diffFile.getAbsolutePath());
                    allMatched = false;
                } else {
                    System.out.println("✅ Matched: " + fileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return allMatched;
    }
}

