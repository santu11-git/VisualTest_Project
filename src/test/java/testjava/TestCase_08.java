package testjava;

import VisualTest.VisualTest.VisualComparisonBatchFolderWiseUtil;
import org.testng.annotations.Test;

public class TestCase_08 {
	
	 @Test
	    public void comparePolicyTypeScreenshots() {  
	        String baselinePath = "C:\\Users\\SREETOMA\\OneDrive\\Desktop\\VisualTest_Project\\VisualTest_Project\\src\\main\\resources\\BaselineMultipleImagesSS\\20250828_111703";
	        String actualPath   = "C:\\Users\\SREETOMA\\OneDrive\\Desktop\\VisualTest_Project\\VisualTest_Project\\src\\main\\resources\\ActualMultipleImagesSS\\20250828_112025";

	        VisualComparisonBatchFolderWiseUtil.compareUserDefinedFolders(baselinePath, actualPath);
	    }

}
