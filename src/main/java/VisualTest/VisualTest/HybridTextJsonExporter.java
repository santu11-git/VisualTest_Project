package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HybridTextJsonExporter {

    public static void exportHybridTextReportToJson(String testUrl) {
        try {
            File folder = new File("src/main/resources/HybridTextValidationResult");
            File latestExcel = getLatestExcel(folder);
            if (latestExcel == null) {
                System.out.println("❌ No hybrid Excel file found.");
                return;
            }

            System.out.println("📂 Reading Hybrid Excel: " + latestExcel.getName());

            // Open workbook and read both sheets
            FileInputStream fis = new FileInputStream(latestExcel);
            Workbook workbook = new XSSFWorkbook(fis);

            List<Map<String, String>> domResult = readSheet(workbook.getSheet("DOM_Result"));
            List<Map<String, String>> ocrResult = readSheet(workbook.getSheet("OCR_Result"));

            Map<String, Object> jsonMap = new LinkedHashMap<>();
            jsonMap.put("url", testUrl);
            jsonMap.put("generated_at", new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()));
            jsonMap.put("dom_result", domResult);
            jsonMap.put("ocr_result", ocrResult);

            // Write to JSON
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String jsonOutput = gson.toJson(jsonMap);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File jsonFile = new File(folder, "Hybrid_Report_" + timestamp + ".json");

            try (FileWriter writer = new FileWriter(jsonFile)) {
                writer.write(jsonOutput);
            }

            workbook.close();
            System.out.println("✅ Hybrid report exported to JSON at: " + jsonFile.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File getLatestExcel(File folder) {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".xlsx"));
        if (files == null || files.length == 0) return null;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0];
    }

    private static List<Map<String, String>> readSheet(Sheet sheet) {
        List<Map<String, String>> list = new ArrayList<>();
        if (sheet == null) return list;

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String baseline = getCellValue(row.getCell(0));
            String actual = getCellValue(row.getCell(1));
            String result = getCellValue(row.getCell(2));

            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("baseline", baseline);
            entry.put("actual", actual);
            entry.put("result", result);

            list.add(entry);
        }
        return list;
    }

    private static String getCellValue(Cell cell) {
        return cell == null ? "" : cell.toString().trim();
    }
}
