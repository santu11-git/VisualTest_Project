package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class A11YExcelReportGenerator {

    public static void writeViolationsToExcel(JSONArray violations, String folderPath, String timestamp) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("A11Y Violations");

        // Header Row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Violation ID");
        headerRow.createCell(1).setCellValue("Rule");
        headerRow.createCell(2).setCellValue("Impact");
        headerRow.createCell(3).setCellValue("WCAG Tags");
        headerRow.createCell(4).setCellValue("Description");
        headerRow.createCell(5).setCellValue("Screenshot");
        headerRow.createCell(6).setCellValue("Help URL");

        // Data Rows
        for (int i = 0; i < violations.length(); i++) {
            JSONObject violation = violations.getJSONObject(i);
            Row row = sheet.createRow(i + 1);

            String violationID = "violation_" + String.format("%02d", i + 1);
            String rule = violation.getString("id");
            String impact = violation.optString("impact", "none");
            String description = violation.getString("description");

            // Parse WCAG tags
            JSONArray tagsArray = violation.optJSONArray("tags");
            StringBuilder tagsBuilder = new StringBuilder();
            if (tagsArray != null) {
                for (int t = 0; t < tagsArray.length(); t++) {
                    if (t > 0) tagsBuilder.append(", ");
                    tagsBuilder.append(tagsArray.getString(t));
                }
            }
            String tags = tagsBuilder.toString();

            String screenshotPath = violationID + "_" + timestamp + ".png";
            String helpUrl = violation.optString("helpUrl", "");

            // Write values into Excel columns
            row.createCell(0).setCellValue(violationID);
            row.createCell(1).setCellValue(rule);
            row.createCell(2).setCellValue(impact);
            row.createCell(3).setCellValue(tags);
            row.createCell(4).setCellValue(description);
            row.createCell(5).setCellValue(screenshotPath);
            row.createCell(6).setCellValue(helpUrl);
        }

        // Autosize columns
        for (int i = 0; i <= 6; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // 🟢 This is where we fix the location:
        String excelFilePath = folderPath + File.separator + "A11Y_Violation_Report_" + timestamp + ".xlsx";

        try (FileOutputStream out = new FileOutputStream(excelFilePath)) {
            workbook.write(out);
            workbook.close();
            System.out.println("✅ Excel report generated at: " + excelFilePath);
        } catch (IOException e) {
            System.out.println("❌ Excel report generation failed: " + e.getMessage());
        }
    }
}
