package VisualTest.VisualTest;

import com.google.gson.Gson;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AITCMistralGherkinClient {

   private static final String API_TOKEN = ""; // Replace with your Hugging Face token
    private static final String MODEL = "mistralai/Mistral-7B-Instruct-v0.3";
    private static final String ENDPOINT = "https://api-inference.huggingface.co/models/" + MODEL;

    public static void callMistralToGenerateGherkin(List<AITCUIElementMeta> elementList) {
        try {
            // Step 1: Build the Prompt
        	AITCPromptBuilder promptBuilder = new AITCPromptBuilder();
            String prompt = promptBuilder.buildPromptFromElements(elementList);

            // Step 2: Build the JSON payload
            Map<String, String> jsonMap = new HashMap<>();
            jsonMap.put("inputs", prompt);
            String requestBody = new Gson().toJson(jsonMap);

            // Step 3: Build the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENDPOINT))
                    .header("Authorization", "Bearer " + API_TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Step 4: Output or save response
            System.out.println("✅ Gherkin Scenarios from Mistral:");
            System.out.println(response.body());

            saveResponseToFile(response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveResponseToFile(String responseBody) {
        try {
            String folder = "src/main/resources/AIResults";
            new File(folder).mkdirs();
            String filename = folder + "/Gherkin_TestCases_" + System.currentTimeMillis() + ".txt";
            try (FileWriter fw = new FileWriter(filename)) {
                fw.write(responseBody);
            }
            System.out.println("📝 Gherkin saved to: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
