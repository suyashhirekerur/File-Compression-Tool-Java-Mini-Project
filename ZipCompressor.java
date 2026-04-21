

import java.io.*;
import java.util.zip.*;

public class ZipCompressor implements ICompressor {

    private static final int BUFFER_SIZE = 8192;

    @Override
    public CompressionResult compress(String sourcePath, String destPath) {
        long startTime = System.currentTimeMillis();
        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            return new CompressionResult(false, "Source file not found.", 0, 0, 0);
        }

        long originalSize = sourceFile.length();

        try (FileOutputStream fos = new FileOutputStream(destPath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            zos.setLevel(Deflater.BEST_COMPRESSION);

            ZipEntry entry = new ZipEntry(sourceFile.getName());
            zos.putNextEntry(entry);

            try (FileInputStream fis = new FileInputStream(sourceFile)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            }

            zos.closeEntry();
            long timeTaken = System.currentTimeMillis() - startTime;
            long outputSize = new File(destPath).length();
            return new CompressionResult(true, "ZIP compression successful.", timeTaken, originalSize, outputSize);

        } catch (IOException e) {
            return new CompressionResult(false, "Error: " + e.getMessage(), 0, originalSize, 0);
        }
    }

    @Override
    public CompressionResult decompress(String sourcePath, String destPath) {
        long startTime = System.currentTimeMillis();
        File sourceFile = new File(sourcePath);
        long originalSize = sourceFile.length();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourcePath))) {
            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {
                File outFile = new File(destPath, entry.getName());

                // Security: prevent Zip Slip
                if (!outFile.getCanonicalPath().startsWith(new File(destPath).getCanonicalPath())) {
                    throw new IOException("Invalid ZIP entry: " + entry.getName());
                }

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }

            long timeTaken = System.currentTimeMillis() - startTime;
            return new CompressionResult(true, "ZIP extraction successful.", timeTaken, originalSize, new File(destPath).length());

        } catch (IOException e) {
            return new CompressionResult(false, "Error: " + e.getMessage(), 0, originalSize, 0);
        }
    }

    @Override
    public String getSupportedExtension() {
        return ".zip";
    }
}
