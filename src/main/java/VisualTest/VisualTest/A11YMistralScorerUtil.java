package VisualTest.VisualTest;

import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;
import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class A11YMistralScorerUtil {

    private static final String API_TOKEN = ""; // Your token
    // Switched to TinyLlama
    private static final String MODEL = "";
    private static final String ENDPOINT = "https://api-inference.huggingface.co/models/" + MODEL;

    public static A11YAIResultDTO callMistralForA11Y(String a11yJsonPathOrDir, String outputDirOrFile) {
        try {
            // --- NEW: Normalize input path for a11yJsonPathOrDir ---
            File inputFile = new File(a11yJsonPathOrDir);
            if (!inputFile.exists()) {
                System.err.println("❌ Input path does not exist: " + a11yJsonPathOrDir);
                return null;
            }

            // If directory is passed, find the latest A11Y JSON inside it
            String finalJsonPath;
            if (inputFile.isDirectory()) {
                File[] jsonFiles = inputFile.listFiles((dir, name) -> name.endsWith(".json") && name.contains("A11Y_Violation_Report"));
                if (jsonFiles == null || jsonFiles.length == 0) {
                    System.err.println("❌ No A11Y violation JSON found in: " + inputFile.getAbsolutePath());
                    return null;
                }
                Arrays.sort(jsonFiles, Comparator.comparingLong(File::lastModified).reversed());
                finalJsonPath = jsonFiles[0].getAbsolutePath();
                System.out.println("✅ Using latest A11Y JSON for scoring: " + finalJsonPath);
            } else {
                finalJsonPath = inputFile.getAbsolutePath();
            }

            // --- Normalize output directory ---
            File outBase = new File(outputDirOrFile);
            if (outBase.isFile()) {
                System.out.println("ℹ️ Provided output path is a file. Using its parent directory: " + outBase.getParent());
                outBase = outBase.getParentFile();
            }

            String jsonInput = Files.readString(Paths.get(finalJsonPath));

            String prompt = "You are an accessibility (A11Y) testing AI assistant.\n" +
                    "Given the following A11Y violations in JSON format, perform the following:\n" +
                    "1. Return a confidence score between 0 and 100 indicating overall accessibility health.\n" +
                    "2. Summarize top 3 most critical violations.\n" +
                    "3. Provide an accessibility readiness badge: PASS, NEEDS REVIEW, or FAIL.\n" +
                    "Here is the JSON:\n" + jsonInput;

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

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path outputPath = Paths.get(outBase.getAbsolutePath(), "AI_A11Y_Score_" + timestamp + ".json");
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, response.body());

            String aiOutput = response.body();
            System.out.println("✅ HuggingFace A11Y Response:");
            System.out.println(aiOutput);
            System.out.println("📝 Response saved to: " + outputPath);

            String confidence = extractConfidence(aiOutput);
            String top3 = extractTop3Summary(aiOutput);
            String badge = extractBadge(aiOutput);

            return new A11YAIResultDTO(confidence, top3, badge, outputPath.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractConfidence(String response) {
        Matcher m = Pattern.compile("(\\d{1,3})").matcher(response);
        if (m.find()) return m.group(1);
        return "Unknown";
    }

    private static String extractTop3Summary(String response) {
        int start = response.indexOf("1.") != -1 ? response.indexOf("1.") : 0;
        return response.substring(start).split("\n3\\.|\\n\\n")[0].trim();
    }

    private static String extractBadge(String response) {
        if (response.toUpperCase().contains("PASS")) return "PASS";
        if (response.toUpperCase().contains("NEEDS REVIEW")) return "NEEDS REVIEW";
        if (response.toUpperCase().contains("FAIL")) return "FAIL";
        return "Unknown";
    }
}
