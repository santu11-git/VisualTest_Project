package VisualTest.VisualTest;

import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class A11YZipUtility {

    /**
     * Compresses the given folder (including all sub-folders/files) into a single .zip file.
     * @param sourceDirPath The directory path to zip (A11Y report folder)
     * @param zipFilePath The output .zip file path
     * @throws IOException if any IO error occurs
     */
    public static void zipFolder(String sourceDirPath, String zipFilePath) throws IOException {
        Path zipPath = Paths.get(zipFilePath);
        try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            Path sourcePath = Paths.get(sourceDirPath);

            Files.walk(sourcePath)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            System.err.println("❌ Failed to zip entry: " + path + " - " + e.getMessage());
                        }
                    });
        }
        System.out.println("✅ Zip file created at: " + zipFilePath);
    }
}
