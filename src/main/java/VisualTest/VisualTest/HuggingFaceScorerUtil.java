package VisualTest.VisualTest;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import com.google.gson.Gson;
import java.util.HashMap;

public class HuggingFaceScorerUtil {

    private static final String API_TOKEN = "	"; // Replace with your token
    private static final String MODEL = "mistralai/Mistral-7B-Instruct-v0.3"; // Mistral Model
    private static final String ENDPOINT = "https://api-inference.huggingface.co/models/" + MODEL; // Mistral End Point

    public static void callMistralOnHuggingFace(String hybridJsonFilePath) {
        try {
            String hybridJson = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(hybridJsonFilePath)));

            String prompt= """
            		You are a visual testing AI assistant.

            		You will be given a hybrid visual text validation result of a web page (includes DOM and OCR results). Based on the analysis, do the following:

            		1. 🔢 Return a confidence score between 0 and 100 — based on the number and severity of mismatches.
            		2. 🧠 Summarize the most important mismatched entries.
            		3. 🏷️ For each mismatch, classify whether it's a "layout issue" or "content issue".
            		4. 💬 Perform a **sentiment analysis** of the visible content and return a one-liner sentiment summary (Positive / Neutral / Negative) with a reason.
            		5. ✍️ Return a **Content Quality Score** from 0 to 100 — based on grammar, repetition, structure, and overall readability of the hybrid content.
            		6. ✅ Provide 1–2 suggestions for improving content quality if applicable.

            		Now process the following JSON and return your output in a clear bullet-point format:

            		JSON Input:
            		""" + hybridJson;

            // Prepare JSON input for HuggingFace
           // String requestBody = "{ \"inputs\": \"" + prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\" }";
            
            Gson gson = new Gson();
            Map<String, String> jsonMap = new HashMap<>();
            jsonMap.put("inputs", prompt);
            String requestBody = gson.toJson(jsonMap);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("✅ HuggingFace Mistral Response:");
            System.out.println(response.body());

            // Optionally, save the result
            saveResponseToFile(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveResponseToFile(String responseBody) {
        try {
            String folder = "src/main/resources/AIResults";
            new File(folder).mkdirs();
            String filename = folder + "/AI_Hybrid_Score_" + System.currentTimeMillis() + ".json";
            try (FileWriter fw = new FileWriter(filename)) {
                fw.write(responseBody);
            }
            System.out.println("📝 Response saved to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
