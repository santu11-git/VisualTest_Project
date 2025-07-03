package VisualTest.VisualTest;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Dimension;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ResponsiveCDPUtil {

	/*
	 * public static void main(String[] args) throws InterruptedException { // TODO
	 * Auto-generated method stub
	 * 
	 * WebDriverManager.chromedriver().setup(); ChromeDriver driver = new
	 * ChromeDriver(); driver.manage().window().maximize();
	 * driver.get("https://sauce-demo.myshopify.com/"); DevTools devtool =
	 * driver.getDevTools(); devtool.createSession();
	 * 
	 * Map<String, Object> deviceMetrics = new HashMap<>();
	 * deviceMetrics.put("width", 768); deviceMetrics.put("height", 1024);
	 * deviceMetrics.put("deviceScaleFactor", 100); deviceMetrics.put("mobile",
	 * true);
	 * 
	 * driver.executeCdpCommand("Emulation.setDeviceMetricsOverride",
	 * deviceMetrics);
	 * 
	 * Thread.sleep(3000);
	 * 
	 * }
	 */


    public static void setMobileView(WebDriver driver) {
    	
    	driver.manage().window().setSize(new Dimension(375, 667)); // iPhone 6/7/8
        System.out.println("Switched to Mobile View");
    }

    public static void setTabletView(WebDriver driver) {
        driver.manage().window().setSize(new Dimension(768, 1024)); // iPad
        System.out.println("Switched to Tablet View");
    }

    public static void setLaptopView(WebDriver driver) {
        driver.manage().window().setSize(new Dimension(1366, 768)); // Typical laptop
        System.out.println("Switched to Laptop View");
    }

    public static void setDesktopView(WebDriver driver) {
        driver.manage().window().setSize(new Dimension(1920, 1080)); // Full HD Desktop
        System.out.println("Switched to Desktop View");
    }

    public static void setCustomView(WebDriver driver, int width, int height) {
        driver.manage().window().setSize(new Dimension(width, height));
        System.out.println("Switched to Custom View: " + width + "x" + height);
    }

}
