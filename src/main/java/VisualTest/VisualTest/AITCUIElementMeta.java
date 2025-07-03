package VisualTest.VisualTest;

import java.util.List;

public class AITCUIElementMeta {
    public String tag;
    public String id;
    public String name;
    public String type;
    public String placeholder;
    public String text;
    public String ariaLabel;
    public String role;
    public String className;
    public List<String> options; // for dropdowns

    public String toPromptString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
            "- Tag: %s\n- ID: %s\n- Name: %s\n- Type: %s\n- Placeholder: %s\n- Text: %s\n- ARIA Label: %s\n- Role: %s\n- Class: %s\n",
            tag, id, name, type, placeholder, text, ariaLabel, role, className
        ));
        if (options != null && !options.isEmpty()) {
            sb.append("- Options: ").append(String.join(", ", options)).append("\n");
        }
        return sb.toString();
    }
}