package VisualTest.VisualTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class A11YViolationJsonWriter {

    public static void writeViolationsToJson(JSONArray violations, String outputDirectory, String timestamp) {
        List<Map<String, Object>> violationList = new ArrayList<>();

        for (int i = 0; i < violations.length(); i++) {
            JSONObject violation = violations.getJSONObject(i);

            Map<String, Object> violationMap = new LinkedHashMap<>();
            violationMap.put("ViolationID", violation.getString("id"));
            violationMap.put("Impact", violation.optString("impact", "none"));
            violationMap.put("Description", violation.getString("description"));
            violationMap.put("HelpUrl", violation.getString("helpUrl"));

            JSONArray nodes = violation.getJSONArray("nodes");

            List<String> selectors = new ArrayList<>();
            if (nodes.length() > 0) {
                JSONObject node = nodes.getJSONObject(0);
                JSONArray targets = node.getJSONArray("target");
                for (int j = 0; j < targets.length(); j++) {
                    selectors.add(targets.getString(j));
                }
            }
            violationMap.put("Selectors", selectors);

            violationList.add(violationMap);
        }

        // Write to JSON file
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(violationList);

        try {
        	// Output the JSON inside the violation folder where screenshots are already saved
        	File violationFolder = new File(outputDirectory + File.separator + "A11Y_Violations_" + timestamp);
        	if (!violationFolder.exists()) {
        	    violationFolder.mkdirs();  // Create folder if not exists
        	}

        	File file = new File(violationFolder, "A11Y_Violations_" + timestamp + ".json");

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(jsonOutput);
            }
            System.out.println("✅ A11Y JSON report saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("❌ Failed to write A11Y JSON report: " + e.getMessage());
        }
    }
}
