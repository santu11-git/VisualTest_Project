package VisualTest.VisualTest;

import org.openqa.selenium.*;
import java.util.*;

public class AITCUIElementExtractor {

    private static final List<String> SUPPORTED_TAGS = Arrays.asList(
        "input", "button", "a", "select", "textarea", "label",
        "form", "div", "span", "img", "svg", "ul", "li", "table", "thead", "tbody", "tr", "td", "th", "nav"
    );

    private static final List<String> SUPPORTED_ROLES = Arrays.asList(
        "button", "link", "textbox", "combobox", "menu", "tab", "checkbox", "radio", "dialog"
    );

    public List<AITCUIElementMeta> extract(WebDriver driver) {
        List<AITCUIElementMeta> elementsList = new ArrayList<>();
        List<WebElement> elements = driver.findElements(By.xpath("//*"));

        for (WebElement el : elements) {
            try {
                String tag = safe(el.getTagName()).toLowerCase();
                String role = safe(el.getAttribute("role")).toLowerCase();

                if (!isSupported(tag, role)) continue;
                if (!el.isDisplayed()) continue;

                AITCUIElementMeta meta = new AITCUIElementMeta();
                meta.tag = tag;
                meta.id = safe(el.getAttribute("id"));
                meta.name = safe(el.getAttribute("name"));
                meta.type = safe(el.getAttribute("type"));
                meta.placeholder = safe(el.getAttribute("placeholder"));
                meta.text = safe(el.getText());
                meta.ariaLabel = safe(el.getAttribute("aria-label"));
                meta.role = role;
                meta.className = safe(el.getAttribute("class"));

                // ✅ If it's a select (dropdown), extract all <option> texts
                if (tag.equals("select")) {
                    List<WebElement> options = el.findElements(By.tagName("option"));
                    List<String> optionValues = new ArrayList<>();
                    for (WebElement opt : options) {
                        String val = opt.getText().trim();
                        if (!val.isEmpty()) {
                            optionValues.add(val);
                        }
                    }
                    meta.options = optionValues;
                }

                elementsList.add(meta);
            } catch (StaleElementReferenceException ignored) {
            }
        }

        return elementsList;
    }

    private boolean isSupported(String tag, String role) {
        return SUPPORTED_TAGS.contains(tag) || SUPPORTED_ROLES.contains(role);
    }

    private String safe(String val) {
        return val != null ? val.trim() : "";
    }
}
