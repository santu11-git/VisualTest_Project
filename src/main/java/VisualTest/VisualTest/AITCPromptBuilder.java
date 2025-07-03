package VisualTest.VisualTest;

import java.util.List;

public class AITCPromptBuilder {

    public String buildPromptFromElements(List<AITCUIElementMeta> elements) {
        StringBuilder sb = new StringBuilder();

        // === LLM Instructions ===
        sb.append("You are a QA test engineer.\n");
        sb.append("Based on the following UI elements, generate Gherkin-style test cases.\n");
        sb.append("Consider scenarios like:\n");
        sb.append("- Valid and invalid input\n");
        sb.append("- Required field validation\n");
        sb.append("- Dropdown value selection and edge cases\n");
        sb.append("- Button actions and navigation\n");
        sb.append("- Error messages for blank/invalid data\n\n");

        int count = 1;
        for (AITCUIElementMeta el : elements) {
            sb.append("Element ").append(count++).append(":\n");
            sb.append(el.toPromptString()).append("\n");
        }

        sb.append("\nRespond with only Gherkin syntax starting with `Scenario:`.\n");

        return sb.toString();
    }

    // Optional: for testing individual element
    public String buildPromptForSingleElement(AITCUIElementMeta el) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a QA test engineer.\n");
        sb.append("Generate 2-3 Gherkin-style test cases for the following UI element:\n\n");
        sb.append(el.toPromptString()).append("\n");
        sb.append("Use Gherkin syntax like `Scenario:` / `Given/When/Then`.\n");
        return sb.toString();
    }
}
