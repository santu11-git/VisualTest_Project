package VisualTest.VisualTest;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class HybridDomOcrComparisonUtil {

    public static void generateHybridTextValidationReport() {
        try {
            File domFolder = new File("src/main/resources/ResultText");
            File ocrFolder = new File("src/main/resources/ResultOCRText");
            File resultFolder = new File("src/main/resources/HybridTextValidationResult");
            if (!resultFolder.exists()) resultFolder.mkdirs();

            File latestDomFile = getLatestExcel(domFolder);
            File latestOcrFile = getLatestExcel(ocrFolder);

            if (latestDomFile == null || latestOcrFile == null) {
                System.out.println("❌ Cannot find DOM or OCR result files.");
                return;
            }

            System.out.println("📂 Using DOM file: " + latestDomFile.getName());
            System.out.println("📂 Using OCR file: " + latestOcrFile.getName());

            Workbook resultWorkbook = new XSSFWorkbook();

            // ✅ Add DOM Sheet
            Sheet domSheet = resultWorkbook.createSheet("DOM_Result");
            copySheetWithComparison(latestDomFile, domSheet);

            // ✅ Add OCR Sheet
            Sheet ocrSheet = resultWorkbook.createSheet("OCR_Result");
            copySheetWithComparison(latestOcrFile, ocrSheet);

            // ✅ Save final result
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String resultPath = "src/main/resources/HybridTextValidationResult/Hybrid_Text_Comparison_" + timestamp + ".xlsx";

            FileOutputStream fos = new FileOutputStream(resultPath);
            resultWorkbook.write(fos);
            resultWorkbook.close();
            fos.close();

            System.out.println("✅ Hybrid DOM + OCR Validation Report saved at: " + resultPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File getLatestExcel(File folder) {
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".xlsx"));
        if (files == null || files.length == 0) return null;
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        return files[0];
    }

    private static void copySheetWithComparison(File sourceFile, Sheet targetSheet) {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             Workbook sourceWorkbook = new XSSFWorkbook(fis)) {

            Sheet sourceSheet = sourceWorkbook.getSheetAt(0);
            int rowCount = sourceSheet.getLastRowNum();

            Row header = targetSheet.createRow(0);
            header.createCell(0).setCellValue("Baseline Text");
            header.createCell(1).setCellValue("Actual Text");
            header.createCell(2).setCellValue("Result");

            for (int i = 1; i <= rowCount; i++) {
                Row sourceRow = sourceSheet.getRow(i);
                if (sourceRow == null) continue;

                Cell baselineCell = sourceRow.getCell(0);
                Cell actualCell = sourceRow.getCell(1);
                String baseline = baselineCell != null ? baselineCell.toString().trim() : "";
                String actual = actualCell != null ? actualCell.toString().trim() : "";
                String result = baseline.equalsIgnoreCase(actual) ? "✅ PASS" : "❌ FAIL";

                Row targetRow = targetSheet.createRow(i);
                targetRow.createCell(0).setCellValue(baseline);
                targetRow.createCell(1).setCellValue(actual);
                targetRow.createCell(2).setCellValue(result);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
