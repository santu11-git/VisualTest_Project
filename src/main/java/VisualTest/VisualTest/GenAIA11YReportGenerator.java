package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.*;

public class GenAIA11YReportGenerator {

    public static void generateVisionAIReport() {
        try {
            // Locate latest output folder automatically
            File outputDir = new File("output");
            File[] folders = outputDir.listFiles(File::isDirectory);

            if (folders == null || folders.length == 0) {
                System.out.println("⚠ No Vision AI folders found.");
                return;
            }

            // Find latest folder based on lastModified
            File latestFolder = folders[0];
            for (File folder : folders) {
                if (folder.lastModified() > latestFolder.lastModified()) {
                    latestFolder = folder;
                }
            }

            String folderPath = latestFolder.getAbsolutePath();

            // Create Excel report
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Vision AI Report");

            // Header row
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Violation ID");
            header.createCell(1).setCellValue("Extracted Text");

            // Read all JSON files inside the folder
            File[] files = latestFolder.listFiles((dir, name) -> name.endsWith(".json"));
            if (files == null || files.length == 0) {
                System.out.println("⚠ No Vision AI JSON files found inside folder.");
                return;
            }

            int rowIndex = 1;
            for (File jsonFile : files) {
                // Skip Axe-Core JSON (we only need Vision AI JSONs here)
                if (jsonFile.getName().startsWith("A11Y_Violations_")) {
                    continue;
                }

                String content = new String(Files.readAllBytes(jsonFile.toPath()));
                JSONObject visionJson = new JSONObject(content);
                String extractedText = visionJson.optString("ExtractedText", "");

                String violationId = jsonFile.getName().replace(".json", "");

                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(violationId);
                row.createCell(1).setCellValue(extractedText);
            }

            // Auto-size columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            // Save Excel
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
