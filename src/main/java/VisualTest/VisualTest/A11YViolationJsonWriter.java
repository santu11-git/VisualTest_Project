package VisualTest.VisualTest;

import org.json.JSONArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;

public class A11YViolationJsonWriter {

    public static void writeViolationsToJson(JSONArray violations, String basePath, String timestamp) {
        try {
            // ✅ Ensure output directory exists from caller
            File folder = new File(basePath);
            if (!folder.exists()) folder.mkdirs();

            // ✅ Prepare JSON structure
            Map<String, Object> result = new LinkedHashMap<>();
            List<Map<String, Object>> violationList = new ArrayList<>();

            for (int i = 0; i < violations.length(); i++) {
                violationList.add(violations.getJSONObject(i).toMap());
            }

            result.put("generated_at", timestamp);
            result.put("violations", violationList);

            // ✅ Generate JSON file path
            String jsonFilePath = basePath + File.separator + "A11Y_Violation_Report_" + timestamp + ".json";

            // ✅ Write to file
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Files.write(Path.of(jsonFilePath), gson.toJson(result).getBytes());

            System.out.println("✅ A11Y JSON file saved at: " + jsonFilePath);

        } catch (Exception e) {
            System.out.println("❌ Failed to write A11Y JSON: " + e.getMessage());
        }
    }
}
