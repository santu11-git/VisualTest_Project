package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class A11YViolationToJsonExporter {

    public static String exportLatestA11YViolationToJson(String outputDir) {
        try {
            File dir = new File(outputDir);
            File[] files = dir.listFiles((d, name) -> name.startsWith("A11Y_Violation_Report_") && name.endsWith(".xlsx"));
            if (files == null || files.length == 0) return null;

            Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
            File latestFile = files[0];

            FileInputStream fis = new FileInputStream(latestFile);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);

            List<Map<String, String>> violations = new ArrayList<>();

            Row headerRow = sheet.getRow(0);
            int cols = headerRow.getLastCellNum();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, String> entry = new LinkedHashMap<>();
                for (int j = 0; j < cols; j++) {
                    Cell keyCell = headerRow.getCell(j);
                    Cell valCell = row.getCell(j);
                    String key = keyCell != null ? keyCell.toString() : "col" + j;
                    String val = valCell != null ? valCell.toString() : "";
                    entry.put(key.trim(), val.trim());
                }
                violations.add(entry);
            }
            workbook.close();

            Map<String, Object> result = new HashMap<>();
            result.put("violations", violations);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String jsonPath = outputDir + File.separator + "A11Y_Violation_Report_" + timestamp + ".json";

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.write(Path.of(jsonPath), gson.toJson(result).getBytes());

            System.out.println("✅ A11Y JSON exported to: " + jsonPath);
            return jsonPath;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
