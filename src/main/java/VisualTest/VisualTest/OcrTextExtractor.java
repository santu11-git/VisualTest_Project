package VisualTest.VisualTest;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class OcrTextExtractor {

    private Tesseract tesseract;
    private static final String OCR_IMAGE_FOLDER = "src/main/resources/OCRTextSS";

    public OcrTextExtractor(String tesseractPath) {
        tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR\\tessdata"); // e.g. "C:/Program Files/Tesseract-OCR/"
        tesseract.setLanguage("eng");
    }

    // ✅ Extract from specific image path (manual)
    public String extractText(String imagePath) {
        try {
            return tesseract.doOCR(new File(imagePath));
        } catch (TesseractException e) {
            e.printStackTrace();
            return "";
        }
    }

    // ✅ Automatically extract text from latest image in OCRTextSS folder
    public String extractTextFromLatestScreenshot() {
        File folder = new File(OCR_IMAGE_FOLDER);
        if (!folder.exists() || folder.listFiles() == null) {
            System.out.println("❌ OCRTextSS folder missing or empty.");
            return "";
        }

        File[] pngFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (pngFiles == null || pngFiles.length == 0) {
            System.out.println("❌ No screenshots found in OCRTextSS.");
            return "";
        }

        File latestImage = Arrays.stream(pngFiles)
                .max(Comparator.comparingLong(File::lastModified))
                .orElse(null);

        if (latestImage == null) {
            System.out.println("❌ No valid image found.");
            return "";
        }

        System.out.println("📸 Using latest screenshot for OCR: " + latestImage.getAbsolutePath());

        try {
            return tesseract.doOCR(latestImage);
        } catch (TesseractException e) {
            e.printStackTrace();
            return "";
        }
    }
}
