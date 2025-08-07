package VisualTest.VisualTest;

public class A11YAIResultDTO {
    public String confidenceScore;
    public String top3Summary;
    public String badge;
    public String aiJsonPath;

    public A11YAIResultDTO(String confidenceScore, String top3Summary, String badge, String aiJsonPath) {
        this.confidenceScore = confidenceScore;
        this.top3Summary = top3Summary;
        this.badge = badge;
        this.aiJsonPath = aiJsonPath;
    }
}
