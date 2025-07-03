package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class OcrTextComparisonUtil {

    public static void compareLatestOCRTextExcels() {
        try {
            File baselineFolder = new File("src/main/resources/OCRBaselineText");
            File actualFolder = new File("src/main/resources/OCRActualText");
            File resultFolder = new File("src/main/resources/ResultOCRText");

            if (!resultFolder.exists()) resultFolder.mkdirs();

            File latestBaseline = getLatestFile(baselineFolder);
            File latestActual = getLatestFile(actualFolder);

            if (latestBaseline == null || latestActual == null) {
                System.out.println("❌ Missing baseline or actual Excel file.");
                return;
            }

            System.out.println("📄 Comparing:\n - Baseline: " + latestBaseline.getName() +
                    "\n - Actual: " + latestActual.getName());

            List<String> baselineText = readColumnText(latestBaseline, 1); // Cleaned_OCR_Text is in column 1
            List<String> actualText = readColumnText(latestActual, 1);     // Cleaned_OCR_Text is in column 1

            int maxRows = Math.max(baselineText.size(), actualText.size());

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String resultPath = "src/main/resources/ResultOCRText/OCR_Comparison_Result_" + timestamp + ".xlsx";

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("OCR Comparison");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Baseline Text");
            header.createCell(1).setCellValue("Actual Text");
            header.createCell(2).setCellValue("Result");

            for (int i = 0; i < maxRows; i++) {
                Row row = sheet.createRow(i + 1);
                String base = i < baselineText.size() ? baselineText.get(i).trim() : "";
                String act = i < actualText.size() ? actualText.get(i).trim() : "";
                String status = base.equalsIgnoreCase(act) ? "✅ PASS" : "❌ FAIL";

                row.createCell(0).setCellValue(base);
                row.createCell(1).setCellValue(act);
                row.createCell(2).setCellValue(status);
            }

            FileOutputStream fos = new FileOutputStream(resultPath);
            workbook.write(fos);
            workbook.close();
            fos.close();

            System.out.println("✅ OCR Text Comparison Result Saved at: " + resultPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File getLatestFile(File folder) {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".xlsx"));
        if (files == null || files.length == 0) return null;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0];
    }

    private static List<String> readColumnText(File excelFile, int columnIndex) {
        List<String> lines = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(excelFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(columnIndex);
                    if (cell != null) {
                        lines.add(cell.toString().trim());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }
}
