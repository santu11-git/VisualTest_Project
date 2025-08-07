package VisualTest.VisualTest;

import com.google.gson.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class A11YBugReportUtil {

    private static final String API_TOKEN = ""; // Your API Token here
    // Use TinyLlama or your preferred model
    private static final String MODEL = "";
    private static final String ENDPOINT = "https://api-inference.huggingface.co/models/" + MODEL;

    public static A11YBugReportDTO generateBugReportExcel(String outputDirOrFile) {
        try {
            // --- NEW: Handle both directory and file path cases ---
            File base = new File(outputDirOrFile);
            if (base.isFile()) {
                // If a file path is passed, switch to its parent directory
                System.out.println("ℹ️ Provided path is a file. Using its parent directory: " + base.getParent());
                base = base.getParentFile();
            }

            // 1. Find the latest A11Y violation JSON file dynamically
            File[] jsonFiles = base.listFiles((dir, name) -> 
                name.endsWith(".json") && name.contains("A11Y_Violation_Report"));
            if (jsonFiles == null || jsonFiles.length == 0) {
                System.err.println("❌ No violation JSON file found in: " + base.getAbsolutePath());
                return null;
            }

            // Sort files by last modified and pick the latest
            Arrays.sort(jsonFiles, Comparator.comparingLong(File::lastModified).reversed());
            String latestJsonPath = jsonFiles[0].getAbsolutePath();
            System.out.println("✅ Using latest violation JSON: " + latestJsonPath);

            String jsonViolations = Files.readString(Paths.get(latestJsonPath));

            String prompt = "You are an expert accessibility QA engineer helping to convert A11Y violations into structured bug reports for Jira.\n" +
                    "Given the following JSON of A11Y violations, provide a JSON array where each object contains:\n" +
                    "- Bug Summary (brief)\n" +
                    "- Description (clear details)\n" +
                    "- Severity (Critical, High, Medium, Low)\n" +
                    "- Steps to Reproduce\n" +
                    "- Expected Result\n" +
                    "- Actual Result\n\nHere is the JSON:\n" + jsonViolations;

            Map<String, Object> payload = new HashMap<>();
            payload.put("inputs", prompt);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String requestJson = gson.toJson(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String aiResponse = response.body();

            JsonArray bugs = extractBugArrayFromResponse(aiResponse);

            if (bugs == null || bugs.size() == 0) {
                System.err.println("❌ AI did not return a valid bug array.");
                return null;
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String excelPath = base.getAbsolutePath() + File.separator + "A11Y_Bug_Report_" + timestamp + ".xlsx";

            writeBugExcel(bugs, excelPath);

            String summary = "Total Bugs: " + bugs.size();
            System.out.println("✅ Bug Report Excel saved at: " + excelPath);

            return new A11YBugReportDTO(excelPath, bugs.size(), summary);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JsonArray extractBugArrayFromResponse(String aiResponse) {
        try {
            String trimmed = aiResponse.trim();
            if (!trimmed.startsWith("{") && !trimmed.startsWith("[")) {
                System.err.println("⚠️ AI response is not JSON. Response was:\n" + aiResponse);
                return null;
            }

            JsonElement root = JsonParser.parseString(aiResponse);
            if (root.isJsonArray()) return root.getAsJsonArray();
            if (root.isJsonObject() && root.getAsJsonObject().has("generated_text")) {
                String text = root.getAsJsonObject().get("generated_text").getAsString();
                return JsonParser.parseString(text).getAsJsonArray();
            }
            return null;
        } catch (Exception e) {
            System.err.println("❌ Failed to parse AI bug response: " + e.getMessage());
            return null;
        }
    }

    private static void writeBugExcel(JsonArray bugs, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("A11Y Bugs");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Summary");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Severity");
        headerRow.createCell(3).setCellValue("Steps to Reproduce");
        headerRow.createCell(4).setCellValue("Expected Result");
        headerRow.createCell(5).setCellValue("Actual Result");

        for (int i = 0; i < bugs.size(); i++) {
            JsonObject bug = bugs.get(i).getAsJsonObject();
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(bug.has("Summary") ? bug.get("Summary").getAsString() : "");
            row.createCell(1).setCellValue(bug.has("Description") ? bug.get("Description").getAsString() : "");
            row.createCell(2).setCellValue(bug.has("Severity") ? bug.get("Severity").getAsString() : "");
            row.createCell(3).setCellValue(bug.has("Steps to Reproduce") ? bug.get("Steps to Reproduce").getAsString() : "");
            row.createCell(4).setCellValue(bug.has("Expected Result") ? bug.get("Expected Result").getAsString() : "");
            row.createCell(5).setCellValue(bug.has("Actual Result") ? bug.get("Actual Result").getAsString() : "");
        }

        for (int i = 0; i <= 5; i++) sheet.autoSizeColumn(i);

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
            workbook.close();
        }
    }
}
