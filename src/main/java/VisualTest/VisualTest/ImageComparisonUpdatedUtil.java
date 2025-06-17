package VisualTest.VisualTest;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.alg.misc.ImageStatistics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class ImageComparisonUpdatedUtil {

    private static final String BASELINE_FOLDER = "src/main/resources/BaselineSS";
    private static final String ACTUAL_FOLDER = "src/main/resources/ActualSS";
    private static final String RESULT_FOLDER = "src/main/resources/ResultSS";
    private static final double SSIM_THRESHOLD = 0.95;

    // ✅ Get latest image file from folder
    private static File getLatestImageFile(File folder) {
        File[] imageFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (imageFiles == null || imageFiles.length == 0) return null;

        return Arrays.stream(imageFiles)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

    // ✅ Main method to compare latest screenshots
    public static boolean compareLatestBaselineAndActual() {
    	
            // ✅ Call fail-safe utility first
            boolean preCheck = FailSafeUtility.validateScreenshotAvailability(BASELINE_FOLDER, ACTUAL_FOLDER);
            if (!preCheck) {
                System.out.println("❌ Pre-check failed. Comparison aborted.");
                return false;
            }
            
        try {
            File baselineImage = getLatestImageFile(new File(BASELINE_FOLDER));
            File actualImage = getLatestImageFile(new File(ACTUAL_FOLDER));

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

            boolean pixelDiff = checkPixelDifference(img1, img2);
            double ssimScore = calculateSSIM(img1, img2);

            System.out.printf("🔬 SSIM Similarity Score: %.5f\n", ssimScore);
            System.out.println("🔎 Pixel Diff Result: " + (pixelDiff ? "No difference" : "Difference found"));

            if (!pixelDiff || ssimScore < SSIM_THRESHOLD) {
                saveDifferenceImage(img1, img2);
                System.out.println("❌ Visual differences detected and result image generated.");
                return false;
            } else {
                System.out.println("✅ Images are visually similar.");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        
    }
            

    // ✅ Simple pixel comparison
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

    // ✅ Simplified SSIM calculation using BoofCV
    private static double calculateSSIM(BufferedImage img1, BufferedImage img2) {
        GrayF32 gray1 = ConvertBufferedImage.convertFrom(img1, (GrayF32) null);
        GrayF32 gray2 = ConvertBufferedImage.convertFrom(img2, (GrayF32) null);

        if (gray1.getWidth() != gray2.getWidth() || gray1.getHeight() != gray2.getHeight()) {
            throw new IllegalArgumentException("Image dimensions do not match for SSIM!");
        }

        double mean1 = ImageStatistics.mean(gray1);
        double mean2 = ImageStatistics.mean(gray2);

        double diff = Math.abs(mean1 - mean2);
        double ssimScore = 1.0 - (diff / 255.0);  // normalize

        return ssimScore;
    }

    // ✅ Generate diff image if mismatch found
    private static void saveDifferenceImage(BufferedImage img1, BufferedImage img2) {
        try {
            BufferedImage diffImage = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < img1.getHeight(); y++) {
                for (int x = 0; x < img1.getWidth(); x++) {
                    int rgb1 = img1.getRGB(x, y);
                    int rgb2 = img2.getRGB(x, y);

                    if (rgb1 != rgb2) {
                        diffImage.setRGB(x, y, Color.RED.getRGB());
                    } else {
                        diffImage.setRGB(x, y, rgb1);
                    }
                }
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File resultFolder = new File(RESULT_FOLDER + "/" + timestamp);
            resultFolder.mkdirs();

            File diffFile = new File(resultFolder, "DiffResult_" + timestamp + ".png");
            ImageIO.write(diffImage, "png", diffFile);
            System.out.println("🖼 Diff image saved at: " + diffFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
