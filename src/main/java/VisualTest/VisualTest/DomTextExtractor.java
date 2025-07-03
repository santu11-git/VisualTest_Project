package VisualTest.VisualTest;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.HashSet;
import java.util.Set;

public class DomTextExtractor {

    private final WebDriver driver;

    public DomTextExtractor(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Extracts all *visible* meaningful text from the DOM.
     */
    public String extractAllDomText() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String domText = (String) js.executeScript(
                "function isVisible(el) {" +
                "  const style = window.getComputedStyle(el);" +
                "  return style && style.display !== 'none' && style.visibility !== 'hidden' && el.offsetParent !== null;" +
                "}" +
                "let walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT, null, false);" +
                "let textSet = new Set();" +
                "while (walker.nextNode()) {" +
                "  let node = walker.currentNode;" +
                "  let parent = node.parentElement;" +
                "  if (!parent) continue;" +
                "  if (['SCRIPT','STYLE','NOSCRIPT'].includes(parent.tagName)) continue;" +
                "  if (!isVisible(parent)) continue;" +
                "  let content = node.nodeValue.trim();" +
                "  if (content.length > 1) textSet.add(content);" +
                "}" +
                "return Array.from(textSet).join('\\n');"
        );

        return domText;
    }
}
