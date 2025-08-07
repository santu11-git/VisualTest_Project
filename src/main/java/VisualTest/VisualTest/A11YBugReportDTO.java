package VisualTest.VisualTest;

public class A11YBugReportDTO {
    public String bugReportPath;
    public int totalBugs;
    public String summary;

    public A11YBugReportDTO(String bugReportPath, int totalBugs, String summary) {
        this.bugReportPath = bugReportPath;
        this.totalBugs = totalBugs;
        this.summary = summary;
    }
}
