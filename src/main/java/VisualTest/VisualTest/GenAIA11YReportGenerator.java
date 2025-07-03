package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.*;

public class GenAIA11YReportGenerator {

    public static void generateVisionAIReport() {
        try {
            // Use consistent output path under Maven structure
            File outputDir = new File("src/main/resources/output");
            File[] folders = outputDir.listFiles(File::isDirectory);

            if (folders == null || folders.length == 0) {
                System.out.println("⚠ No Vision AI folders found in: " + outputDir.getAbsolutePath());
                return;
            }

            // Find the most recently modified folder
            File latestFolder = folders[0];
            for (File folder : folders) {
                if (folder.lastModified() > latestFolder.lastModified()) {
                    latestFolder = folder;
                }
            }

            String folderPath = latestFolder.getAbsolutePath();

            // Create Excel workbook
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Vision AI Report");

            // Create header
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Violation ID");
            header.createCell(1).setCellValue("Extracted Text");

            // Read all relevant JSON files inside latest folder
            File[] jsonFiles = latestFolder.listFiles((dir, name) -> name.endsWith(".json"));
            if (jsonFiles == null || jsonFiles.length == 0) {
                System.out.println("⚠ No Vision AI JSON files found inside: " + folderPath);
                return;
            }

            int rowIndex = 1;
            for (File jsonFile : jsonFiles) {
                // Skip axe-core JSONs
                if (jsonFile.getName().startsWith("A11Y_Violation_Report")) continue;

                String content = Files.readString(jsonFile.toPath()).trim();

                // Validate JSON
                if (content.isEmpty() || !content.startsWith("{")) {
                    System.out.println("⚠ Skipping invalid JSON: " + jsonFile.getName());
                    continue;
                }

                try {
                    JSONObject visionJson = new JSONObject(content);
                    String extractedText = visionJson.optString("ExtractedText", "");
                    String violationId = jsonFile.getName().replace(".json", "");

                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(violationId);
                    row.createCell(1).setCellValue(extractedText);

                } catch (Exception e) {
                    System.out.println("⚠ Failed to parse JSON in " + jsonFile.getName() + ": " + e.getMessage());
                }
            }

            // Resize columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            // Save Excel to: src/main/resources/output/.../VisionAI_Report.xlsx
            String excelPath = folderPath + File.separator + "VisionAI_Report.xlsx";
            try (FileOutputStream out = new FileOutputStream(excelPath)) {
                workbook.write(out);
                workbook.close();
                System.out.println("✅ Vision AI report generated: " + excelPath);
            }

        } catch (Exception e) {
            System.out.println("❌ Failed to generate Vision AI report: " + e.getMessage());
        }
    }
}
