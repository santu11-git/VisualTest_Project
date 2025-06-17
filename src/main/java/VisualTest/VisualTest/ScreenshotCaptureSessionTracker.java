package VisualTest.VisualTest;

public class ScreenshotCaptureSessionTracker {
    private static String runTimestamp;

    public static String getTimestamp() {
        if (runTimestamp == null) {
            runTimestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        }
        return runTimestamp;
    }

    public static void resetTimestamp() {
        runTimestamp = null;
    }
}