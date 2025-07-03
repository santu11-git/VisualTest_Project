package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DomTextComparator {

    public static void compareDomTextExcelFiles() {
        try {
            String baselineFolder = "src/main/resources/BaselineText";
            String actualFolder = "src/main/resources/ActualText";
            String resultFolder = "src/main/resources/ResultText";

            // 1. Ensure ResultText folder exists
            new File(resultFolder).mkdirs();

            // 2. Get latest baseline and actual Excel file
            File latestBaseline = getLatestFile(baselineFolder, "DOM_Baseline");
            File latestActual = getLatestFile(actualFolder, "DOM_Actual");

            if (latestBaseline == null || latestActual == null) {
                System.err.println("❌ Missing baseline or actual Excel file.");
                return;
            }

            System.out.println("📄 Baseline: " + latestBaseline.getName());
            System.out.println("📄 Actual  : " + latestActual.getName());

            // 3. Load Excel data into lists
            List<String> baselineText = readDomTextFromExcel(latestBaseline);
            List<String> actualText = readDomTextFromExcel(latestActual);

            // 4. Create result workbook
            Workbook resultWorkbook = new XSSFWorkbook();
            Sheet resultSheet = resultWorkbook.createSheet("DOM Text Comparison");

            // 5. Header row
            Row header = resultSheet.createRow(0);
            header.createCell(0).setCellValue("Baseline_DOM");
            header.createCell(1).setCellValue("Actual_DOM");
            header.createCell(2).setCellValue("Result");

            // 6. Compare and write result
            int max = Math.max(baselineText.size(), actualText.size());
            for (int i = 0; i < max; i++) {
                String baselineLine = i < baselineText.size() ? baselineText.get(i).trim() : "";
                String actualLine = i < actualText.size() ? actualText.get(i).trim() : "";

                Row row = resultSheet.createRow(i + 1);
                row.createCell(0).setCellValue(baselineLine);
                row.createCell(1).setCellValue(actualLine);

                if (baselineLine.equalsIgnoreCase(actualLine)) {
                    row.createCell(2).setCellValue("✅ Pass");
                } else {
                    row.createCell(2).setCellValue("❌ Fail");
                }
            }

            // 7. Save result Excel
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String resultPath = resultFolder + "/DOM_Result_" + timestamp + ".xlsx";

            FileOutputStream out = new FileOutputStream(resultPath);
            resultWorkbook.write(out);
            out.close();
            resultWorkbook.close();

            System.out.println("✅ Comparison complete. Result saved to: " + resultPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Error during DOM text comparison.");
        }
    }

    // Helper to get latest file with prefix
    private static File getLatestFile(String folderPath, String prefix) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.startsWith(prefix) && name.endsWith(".xlsx"));
        if (files == null || files.length == 0) return null;
        return Arrays.stream(files).max(Comparator.comparingLong(File::lastModified)).orElse(null);
    }

    // Helper to read Excel column 0 (DOM_Text) into list
    private static List<String> readDomTextFromExcel(File file) throws IOException {
        List<String> lines = new ArrayList<>();
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        // Start from row 1 (skip header)
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell cell = row.getCell(0);
            if (cell != null) lines.add(cell.toString());
        }

        workbook.close();
        return lines;
    }
}