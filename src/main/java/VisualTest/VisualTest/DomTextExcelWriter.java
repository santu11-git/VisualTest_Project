package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DomTextExcelWriter {

    public static void saveDomTextToExcel(String domText) {
        try {
            // 1. Prepare folder path
            String folderPath = "src/main/resources/BaselineText";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
                System.out.println("📁 Created folder: " + folderPath);
            }

            // 2. Generate timestamped filename
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String excelPath = folderPath + "/DOM_Baseline_" + timestamp + ".xlsx";

            // 3. Create Excel Workbook & Sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Baseline DOM Text");

            // 4. Create header
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue("DOM_Text");

            // 5. Write line-by-line DOM text
            String[] lines = domText.split("\n");
            int rowIndex = 1; // Start after header

            for (String line : lines) {
                if (line.trim().isEmpty()) continue; // ❌ Skip empty or whitespace-only lines
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(line.trim());
            }

            // 6. Write to file
            FileOutputStream fileOut = new FileOutputStream(excelPath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("✅ DOM text saved to: " + excelPath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ Failed to write DOM text to Excel.");
        }
    }
}