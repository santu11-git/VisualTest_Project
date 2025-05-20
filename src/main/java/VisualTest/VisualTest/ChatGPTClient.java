package VisualTest.VisualTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;
import okhttp3.*;
import java.nio.file.Files;
import java.nio.file.Path;

import org.json.JSONArray;
import org.json.JSONObject;


public class ChatGPTClient {

    private static String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static String MODEL;
    private static String API_KEY;

    static {
        Properties props = new Properties();
        try (InputStream input = ChatGPTClient.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in classpath");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
        MODEL = props.getProperty("model");
        API_KEY = props.getProperty("apiKey");
    }

	/*
	 * public String getChatCompletion(String userPrompt) { OkHttpClient client =
	 * new OkHttpClient();
	 * 
	 * String requestBodyStr = String.format( "{\n" + "  \"model\": \"%s\",\n" +
	 * "  \"messages\": [\n" + "    {\n" + "      \"role\": \"user\",\n" +
	 * "      \"content\": \"%s\"\n" + "    }\n" + "  ]\n" + "}", MODEL,
	 * userPrompt.replace("\"", "\\\""));
	 * 
	 * RequestBody body = RequestBody.create( requestBodyStr,
	 * MediaType.get("application/json; charset=utf-8"));
	 * 
	 * Request request = new Request.Builder() .url(OPENAI_API_URL)
	 * .header("Authorization", "Bearer " + API_KEY) .header("Content-Type",
	 * "application/json") .post(body) .build();
	 * 
	 * try (Response response = client.newCall(request).execute()) { if
	 * (!response.isSuccessful()) { throw new IOException("Unexpected response: " +
	 * response); } return response.body().string(); } catch (IOException e) {
	 * e.printStackTrace(); return "Error: " + e.getMessage(); } }
	 */
    
    public String getChatCompletion(String userPrompt) {
    	OkHttpClient client = new OkHttpClient.Builder()
    		    .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
    		    .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
    		    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
    		    .build();
    	
        String requestBodyStr = String.format(
            "{\n" +
            "  \"model\": \"%s\",\n" +
            "  \"messages\": [\n" +
            "    {\n" +
            "      \"role\": \"user\",\n" +
            "      \"content\": \"%s\"\n" +
            "    }\n" +
            "  ]\n" +
            "}", MODEL, userPrompt.replace("\"", "\\\""));

        RequestBody body = RequestBody.create(
                requestBodyStr,
                MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
            .url(OPENAI_API_URL)
            .header("Authorization", "Bearer " + API_KEY)
            .header("Content-Type", "application/json")
            .post(body)
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }

            String jsonResponse = response.body().string();
            JSONObject json = new JSONObject(jsonResponse);

            // Extract the content of the first assistant message
            return json.getJSONArray("choices")
                       .getJSONObject(0)
                       .getJSONObject("message")
                       .getString("content");

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        
    }
    

    	    static {
    	        Properties props = new Properties();
    	        try (InputStream input = ChatGPTClient.class.getClassLoader().getResourceAsStream("config.properties")) {
    	            if (input == null) throw new RuntimeException("config.properties not found in classpath");
    	            props.load(input);
    	        } catch (IOException e) {
    	            throw new RuntimeException("Failed to load config.properties", e);
    	        }
    	        MODEL = props.getProperty("model");
    	        API_KEY = props.getProperty("apiKey");
    	    }

    	    // Text-based UI anomaly detection
    	    public String detectTextAnomalies(String prompt) {
    	        OkHttpClient client = new OkHttpClient();

    	        try {
    	            JSONArray messages = new JSONArray()
    	                .put(new JSONObject().put("role", "user").put("content", prompt));

    	            JSONObject payload = new JSONObject()
    	                .put("model", MODEL)
    	                .put("messages", messages);

    	            RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json"));

    	            Request request = new Request.Builder()
    	                .url(OPENAI_API_URL)
    	                .header("Authorization", "Bearer " + API_KEY)
    	                .post(body)
    	                .build();

    	            try (Response response = client.newCall(request).execute()) {
    	                if (!response.isSuccessful())
    	                    throw new IOException("Unexpected response: " + response);

    	                JSONObject json = new JSONObject(response.body().string());
    	                return json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
    	            }

    	        } catch (IOException e) {
    	            return "Error: " + e.getMessage();
    	        }
    	    }
    	    public String detectImageAnomalies(File imageFile, String prompt) {
    	        OkHttpClient client = new OkHttpClient();

    	        try {
    	            // Read image bytes
    	            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());

    	            // Build the message array for GPT vision
    	            JSONArray messages = new JSONArray()
    	                .put(new JSONObject()
    	                    .put("role", "user")
    	                    .put("content", new JSONArray()
    	                        .put(new JSONObject().put("type", "text").put("text", prompt))
    	                        .put(new JSONObject()
    	                            .put("type", "image_url")
    	                            .put("image_url", new JSONObject()
    	                                .put("url", "data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(imageBytes))
    	                                .put("detail", "low"))))); // "low" to reduce cost/latency

    	            // Assemble the request
    	            JSONObject payload = new JSONObject()
    	                .put("model", MODEL) // e.g., gpt-4-vision-preview
    	                .put("messages", messages);

    	            RequestBody body = RequestBody.create(payload.toString(), MediaType.get("application/json"));

    	            Request request = new Request.Builder()
    	                .url(OPENAI_API_URL)
    	                .header("Authorization", "Bearer " + API_KEY)
    	                .post(body)
    	                .build();

    	            // Execute the request
    	            try (Response response = client.newCall(request).execute()) {
    	                if (!response.isSuccessful()) {
    	                    System.err.println("❌ GPT Vision request failed: " + response.code() + " " + response.message());
    	                    return "Error: Unexpected response: " + response;
    	                }

    	                JSONObject json = new JSONObject(response.body().string());
    	                return json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
    	            }

    	        } catch (IOException e) {
    	            e.printStackTrace();
    	            return "Error: " + e.getMessage();
    	        }
    	    }
    	}

    
    
