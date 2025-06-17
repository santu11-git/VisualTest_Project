package VisualTest.VisualTest;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.Feature;
import com.google.protobuf.ByteString;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VisionAIUtil {

    public static void analyzeImage(File imageFile, File screenshotFolder) {
    	
    	// This part is for Mock demo controlled by flag. 
    		{
            if (VisionAIConfig.MOCK_MODEE) {
                VisionAIStub.processMockVisionAI(imageFile, screenshotFolder);
                return;
            }
            // Real Google Vision AI code as earlier
        try {
            ByteString imgBytes = ByteString.readFrom(new FileInputStream(imageFile));

            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();

            Feature feature = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();

            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder()
                            .addFeatures(feature)
                            .setImage(img)
                            .build();
            requests.add(request);

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                AnnotateImageResponse response = client.batchAnnotateImages(requests).getResponsesList().get(0);
                if (response.hasError()) {
                    System.out.printf("❌ Vision API Error: %s\n", response.getError().getMessage());
                    return;
                }

                String extractedText = response.getFullTextAnnotation().getText();
                System.out.println("✅ Extracted Text:");
                System.out.println(extractedText);

                // Save the output JSON
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String jsonOutput = gson.toJson(Map.of("ExtractedText", extractedText));

                File jsonFile = new File(screenshotFolder, imageFile.getName().replace(".png", ".json"));
                try (FileOutputStream out = new FileOutputStream(jsonFile)) {
                    out.write(jsonOutput.getBytes());
                }

                System.out.println("✅ Vision result saved to: " + jsonFile.getAbsolutePath());
            }

        } catch (IOException e) {
            System.out.println("❌ Vision API exception: " + e.getMessage());
        }
    		}
    }
}
    
