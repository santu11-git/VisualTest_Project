package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DomActualExcelWriter {

    public static void saveDomTextToExcel(String domText) {
        try {
            // 1. Prepare folder
            String folderPath = "src/main/resources/ActualText";
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
                System.out.println("📁 Created folder: " + folderPath);
            }

            // 2. Prepare timestamped file path
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String excelPath = folderPath + "/DOM_Actual_" + timestamp + ".xlsx";

            // 3. Create Excel workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Actual DOM Text");

            // 4. Add header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("DOM_Text");

            // 5. Clean and write non-empty lines
            String[] lines = domText.split("\n");
            int rowIndex = 1;
            for (String line : lines) {
                if (line.trim().isEmpty()) continue; // ❌ Skip blanks
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(line.trim());
            }

            // 6. Write Excel to disk
            FileOutputStream fileOut = new FileOutputStream(excelPath);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("✅ Actual DOM text saved to: " + excelPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Failed to write Actual DOM text to Excel.");
        }
    }
}