package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class OcrBaselineExcelWriter {

    public static void writeToExcel(String ocrText) {
        try {
            String folderPath = "src/main/resources/OCRBaselineText";
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filePath = folderPath + "/OCR_Baseline_" + timestamp + ".xlsx";

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("OCR Text");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Raw_OCR_Text");
            headerRow.createCell(1).setCellValue("Cleaned_OCR_Text");

            String[] lines = ocrText.split("\\r?\\n");
            Set<String> uniqueCleanedLines = new LinkedHashSet<>(); // To avoid duplicates
            int rowIndex = 1;

            for (String line : lines) {
                String raw = line.trim();
                if (!raw.isEmpty()) {
                    // 🧹 Clean the line (filtering unwanted characters and spaces)
                    String cleaned = raw.replaceAll("[^\\x00-\\x7F]", "")  // Remove non-ASCII
                                        .replaceAll("[^\\p{L}\\p{N}\\s,.!?:;'\"()-]", "") // Remove unwanted symbols
                                        .replaceAll("\\s{2,}", " ") // Collapse multiple spaces
                                        .trim();

                    if (!cleaned.isEmpty()) {
                        uniqueCleanedLines.add(cleaned);
                        Row row = sheet.createRow(rowIndex++);
                        row.createCell(0).setCellValue(raw);
                        row.createCell(1).setCellValue(cleaned);
                    }
                }
            }

            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.close();
            workbook.close();

            System.out.println("✅ OCR Baseline Excel Saved at: " + filePath);
            System.out.println("📄 Filtered OCR Text (Cleaned Preview):\n");
            uniqueCleanedLines.forEach(line -> System.out.println("• " + line));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
