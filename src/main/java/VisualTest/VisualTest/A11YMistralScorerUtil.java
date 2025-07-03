package VisualTest.VisualTest;

import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class A11YMistralScorerUtil {

	private static final String API_TOKEN = ""; // Replace with your token
    private static final String MODEL = "mistralai/Mistral-7B-Instruct-v0.3"; // Mistral Model
    private static final String ENDPOINT = "https://api-inference.huggingface.co/models/" + MODEL; // Mistral End Point

    public static void callMistralForA11Y(String a11yJsonPath) {
        try {
            String jsonInput = Files.readString(Paths.get(a11yJsonPath));

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

            String timestamp = String.valueOf(System.currentTimeMillis());
            String outputPath = "src/main/resources/AIResults/AI_A11Y_Score_" + timestamp + ".json";
            Files.writeString(Path.of(outputPath), response.body());

            System.out.println("✅ HuggingFace A11Y Response:");
            System.out.println(response.body());
            System.out.println("📝 Response saved to: " + outputPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
