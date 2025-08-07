package VisualTest.VisualTest;

import boofcv.alg.filter.blur.GBlurImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class ImageComparisonUpdatedUtil {

    private static final String BASE_TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "VisualIQ";
    private static final File BASELINE_PARENT_FOLDER = new File(BASE_TEMP_DIR + File.separator + "BaselineSS");
    private static final File ACTUAL_PARENT_FOLDER = new File(BASE_TEMP_DIR + File.separator + "ActualSS");
    private static final File RESULT_FOLDER = new File(BASE_TEMP_DIR + File.separator + "ResultSS");

    private static final double SSIM_THRESHOLD = 0.95;
    private static final int COLOR_TOLERANCE = 25;
    private static final int PATCH_DIFF_THRESHOLD = 4;

    public static boolean compareLatestBaselineAndActual() {
        File latestBaselineImage = getLatestImageFromTimestampedFolder(BASELINE_PARENT_FOLDER);
        File latestActualImage = getLatestImageFromTimestampedFolder(ACTUAL_PARENT_FOLDER);

        if (latestBaselineImage == null || latestActualImage == null) {
            System.out.println("❌ Could not locate latest baseline or actual image.");
            return false;
        }

        try {
            BufferedImage img1 = ImageIO.read(latestBaselineImage);
            BufferedImage img2 = ImageIO.read(latestActualImage);

            int width = Math.min(img1.getWidth(), img2.getWidth());
            int height = Math.min(img1.getHeight(), img2.getHeight());

            BufferedImage cropped1 = img1.getSubimage(0, 0, width, height);
            BufferedImage cropped2 = img2.getSubimage(0, 0, width, height);

            boolean pixelSimilar = checkPixelDifference(cropped1, cropped2);
            double ssimScore = calculateSimplifiedSSIM(cropped1, cropped2);

            System.out.printf("🔬 SSIM Similarity Score: %.5f\n", ssimScore);
            System.out.println("🔎 Pixel Diff Result: " + (pixelSimilar ? "No significant diff" : "Difference found"));

            if (!pixelSimilar || ssimScore < SSIM_THRESHOLD) {
                saveDifferenceImage(cropped1, cropped2);
                System.out.println("❌ Differences detected; result image generated.");
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

    // 🆕 This method locates the latest timestamped folder and fetches the latest image
    private static File getLatestImageFromTimestampedFolder(File parentFolder) {
        File[] timestampedFolders = parentFolder.listFiles(File::isDirectory);
        if (timestampedFolders == null || timestampedFolders.length == 0) return null;

        File latestFolder = Arrays.stream(timestampedFolders)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);

        if (latestFolder == null) return null;

        File[] imageFiles = latestFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (imageFiles == null || imageFiles.length == 0) return null;

        return Arrays.stream(imageFiles)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

    private static boolean checkPixelDifference(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth(), height = img1.getHeight();

        for (int y = 2; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {
                int diffCount = 0;

                for (int dy = -2; dy <= 2; dy++) {
                    for (int dx = -2; dx <= 2; dx++) {
                        int rgb1 = img1.getRGB(x + dx, y + dy);
                        int rgb2 = img2.getRGB(x + dx, y + dy);

                        int r1 = (rgb1 >> 16) & 0xFF, g1 = (rgb1 >> 8) & 0xFF, b1 = rgb1 & 0xFF;
                        int r2 = (rgb2 >> 16) & 0xFF, g2 = (rgb2 >> 8) & 0xFF, b2 = rgb2 & 0xFF;

                        int diff = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                        if (diff > COLOR_TOLERANCE) diffCount++;
                    }
                }

                if (diffCount > PATCH_DIFF_THRESHOLD) {
                    return false;
                }
            }
        }

        return true;
    }

    private static double calculateSimplifiedSSIM(BufferedImage img1, BufferedImage img2) {
        GrayF32 gray1 = ConvertBufferedImage.convertFrom(img1, (GrayF32) null);
        GrayF32 gray2 = ConvertBufferedImage.convertFrom(img2, (GrayF32) null);

        GBlurImageOps.gaussian(gray1, gray1, -1, 2, null);
        GBlurImageOps.gaussian(gray2, gray2, -1, 2, null);

        double mean1 = ImageStatistics.mean(gray1);
        double mean2 = ImageStatistics.mean(gray2);

        double diff = Math.abs(mean1 - mean2);
        return 1.0 - (diff / 255.0);
    }

    private static void saveDifferenceImage(BufferedImage img1, BufferedImage img2) {
        try {
            int width = img1.getWidth();
            int height = img1.getHeight();
            BufferedImage diffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb1 = img1.getRGB(x, y);
                    int rgb2 = img2.getRGB(x, y);

                    int r1 = (rgb1 >> 16) & 0xFF, g1 = (rgb1 >> 8) & 0xFF, b1 = rgb1 & 0xFF;
                    int r2 = (rgb2 >> 16) & 0xFF, g2 = (rgb2 >> 8) & 0xFF, b2 = rgb2 & 0xFF;

                    int diff = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                    diffImage.setRGB(x, y, diff > COLOR_TOLERANCE ? Color.RED.getRGB() : rgb1);
                }
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File resultFolder = new File(RESULT_FOLDER + File.separator + timestamp);
            resultFolder.mkdirs();

            File diffFile = new File(resultFolder, "DiffResult_" + timestamp + ".png");
            ImageIO.write(diffImage, "png", diffFile);
            System.out.println("🖼 Diff image saved at: " + diffFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}