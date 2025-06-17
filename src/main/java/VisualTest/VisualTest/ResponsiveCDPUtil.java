package VisualTest.VisualTest;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ResponsiveCDPUtil {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		WebDriverManager.chromedriver().setup();
		ChromeDriver driver = new ChromeDriver();
		driver.manage().window().maximize();
		
		DevTools devtool = driver.getDevTools();
		devtool.createSession();
		
		Map<String, Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", 768);
        deviceMetrics.put("height", 1024);
        deviceMetrics.put("deviceScaleFactor", 100);
        deviceMetrics.put("mobile", true);

        driver.executeCdpCommand("Emulation.setDeviceMetricsOverride", deviceMetrics);
        
        driver.get("https://sauce-demo.myshopify.com/");
        
        Thread.sleep(3000);
		
	}

}
