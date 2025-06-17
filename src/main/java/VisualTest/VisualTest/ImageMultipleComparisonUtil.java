package VisualTest.VisualTest;

import java.io.File;
import java.util.Arrays;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;

public class ImageMultipleComparisonUtil {

    private static final String BASELINE_DIR = "src/main/resources/BaselineSS";
    private static final String ACTUAL_DIR = "src/main/resources/ActualSS";
    private static final String RESULT_DIR = "src/main/resources/ResultSS";

    private static File getLatestFolder(String path) {
        File dir = new File(path);
        File[] folders = dir.listFiles(File::isDirectory);
        if (folders == null || folders.length == 0) return null;

        return Arrays.stream(folders)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);
    }

    public static boolean compareAllMatchingImages() {
        try {
            File latestBaselineFolder = getLatestFolder(BASELINE_DIR);
            File latestActualFolder = getLatestFolder(ACTUAL_DIR);

            if (latestBaselineFolder == null || latestActualFolder == null) {
                System.out.println("❌ Could not find latest folders in BaselineSS or ActualSS.");
                return false;
            }

            File[] baselineImages = latestBaselineFolder.listFiles((dir, name) -> name.endsWith(".png"));
            File[] actualImages = latestActualFolder.listFiles((dir, name) -> name.endsWith(".png"));

            if (baselineImages == null || actualImages == null) {
                System.out.println("❌ No PNG images found in the folders.");
                return false;
            }

            Map<String, File> actualMap = Arrays.stream(actualImages)
                    .collect(Collectors.toMap(File::getName, file -> file));

            String resultFolder = RESULT_DIR + "/" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_Result";
            new File(resultFolder).mkdirs();

            boolean allMatch = true;

            for (File baseImage : baselineImages) {
                String imageName = baseImage.getName();
                if (actualMap.containsKey(imageName)) {
                    BufferedImage img1 = ImageIO.read(baseImage);
                    BufferedImage img2 = ImageIO.read(actualMap.get(imageName));

                    boolean isSame = true;
                    BufferedImage diff = new BufferedImage(img1.getWidth(), img1.getHeight(), BufferedImage.TYPE_INT_RGB);

                    for (int y = 0; y < img1.getHeight(); y++) {
                        for (int x = 0; x < img1.getWidth(); x++) {
                            int rgb1 = img1.getRGB(x, y);
                            int rgb2 = img2.getRGB(x, y);
                            if (rgb1 != rgb2) {
                                diff.setRGB(x, y, Color.RED.getRGB());
                                isSame = false;
                            } else {
                                diff.setRGB(x, y, rgb1);
                            }
                        }
                    }

                    if (!isSame) {
                        File diffFile = new File(resultFolder + "/" + imageName.replace(".png", "_Diff.png"));
                        ImageIO.write(diff, "png", diffFile);
                        System.out.println("❌ Difference found in " + imageName + " → saved diff.");
                        allMatch = false;
                    } else {
                        System.out.println("✅ " + imageName + " matches.");
                    }
                } else {
                    System.out.println("⚠️ Matching image not found in Actual for: " + imageName);
                    allMatch = false;
                }
            }

            return allMatch;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
