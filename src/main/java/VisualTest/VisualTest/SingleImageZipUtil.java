package VisualTest.VisualTest;

import java.awt.Desktop;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;

public class SingleImageZipUtil {

    private static final String BASE_DIR = System.getProperty("java.io.tmpdir") + File.separator + "VisualIQ";
    private static final String[] TARGET_FOLDERS = { "BaselineSS", "ActualSS", "ResultSS" };

    public static String createLatestVisualIQZip() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File zipFile = new File(BASE_DIR, "VisualIQ_Images_" + timestamp + ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                for (String folderName : TARGET_FOLDERS) {
                    File parent = new File(BASE_DIR, folderName);
                    File latest = getLatestTimestampedFolder(parent);
                    if (latest != null) {
                        File[] files = latest.listFiles((dir, name) -> name.endsWith(".png"));
                        if (files != null && files.length > 0) {
                            for (File img : files) {
                                String zipEntryName = folderName + "/" + latest.getName() + "/" + img.getName();
                                addFileToZip(img, zipEntryName, zos);
                            }
                        }
                    }
                }
            }

            System.out.println("📦 Zip file created at: " + zipFile.getAbsolutePath());

            // Optional: open file location if desktop supported
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(zipFile.getParentFile());
            }

        } catch (Exception e) {
            System.err.println("❌ Error creating ZIP: " + e.getMessage());
            e.printStackTrace();
        }
		return null;
    }

    private static void addFileToZip(File file, String entryName, ZipOutputStream zos) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry entry = new ZipEntry(entryName);
            zos.putNextEntry(entry);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
        }
    }

    private static File getLatestTimestampedFolder(File parentFolder) {
        File[] folders = parentFolder.listFiles(File::isDirectory);
        if (folders == null || folders.length == 0) return null;

        return Arrays.stream(folders)
                .filter(f -> f.getName().matches("\\d{8}_\\d{6}.*")) // e.g., 20250804_140000_Actual
                .max(Comparator.comparing(File::getName)) // alphabetical = chronological in timestamp
                .orElse(null);
    }
}
