package VisualTest.VisualTest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class VisualTest04_TimeStamp {
	
	public String TimeStamp_Logic() throws Exception {
	
	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	String fileName = "Baseline_" + timeStamp + ".png";
	return fileName;
	}

}
