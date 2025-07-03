package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class OcrActualExcelWriter {

    public static void writeToExcel(String ocrText) {
        try {
            String folderPath = "src/main/resources/OCRActualText";
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filePath = folderPath + "/OCR_Actual_" + timestamp + ".xlsx";

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("OCR Text");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Raw_OCR_Text");
            headerRow.createCell(1).setCellValue("Cleaned_OCR_Text");

            String[] lines = ocrText.split("\\r?\\n");
            Set<String> uniqueCleanedLines = new LinkedHashSet<>();
            int rowIndex = 1;

            for (String line : lines) {
                String raw = line.trim();
                if (!raw.isEmpty()) {
                    String cleaned = raw.replaceAll("[^\\x00-\\x7F]", "")  // remove non-ASCII
                                        .replaceAll("[^\\p{L}\\p{N}\\s,.!?:;'\"()-]", "") // remove extra junk
                                        .replaceAll("\\s{2,}", " ") // collapse multiple spaces
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

            System.out.println("✅ OCR Actual Excel Saved at: " + filePath);
            System.out.println("📄 Filtered OCR Text (Cleaned Preview):\n");
            uniqueCleanedLines.forEach(line -> System.out.println("• " + line));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
