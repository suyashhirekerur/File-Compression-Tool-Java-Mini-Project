

import java.io.*;
import java.util.zip.*;

public class GzipCompressor implements ICompressor {

    private static final int BUFFER_SIZE = 8192;

    @Override
    public CompressionResult compress(String sourcePath, String destPath) {
        long startTime = System.currentTimeMillis();
        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            return new CompressionResult(false, "Source file not found.", 0, 0, 0);
        }

        long originalSize = sourceFile.length();

        try (FileInputStream fis = new FileInputStream(sourceFile);
             GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(destPath))) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                gos.write(buffer, 0, len);
            }

            long timeTaken = System.currentTimeMillis() - startTime;
            long outputSize = new File(destPath).length();
            return new CompressionResult(true, "GZIP compression successful.", timeTaken, originalSize, outputSize);

        } catch (IOException e) {
            return new CompressionResult(false, "Error: " + e.getMessage(), 0, originalSize, 0);
        }
    }

    @Override
    public CompressionResult decompress(String sourcePath, String destPath) {
        long startTime = System.currentTimeMillis();
        File sourceFile = new File(sourcePath);
        long originalSize = sourceFile.length();

        // Determine output filename (remove .gz extension)
        String outputFile = destPath + File.separator +
            sourceFile.getName().replace(".gz", "");

        try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(sourcePath));
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = gis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            long timeTaken = System.currentTimeMillis() - startTime;
            long outputSize = new File(outputFile).length();
            return new CompressionResult(true, "GZIP extraction successful.", timeTaken, originalSize, outputSize);

        } catch (IOException e) {
            return new CompressionResult(false, "Error: " + e.getMessage(), 0, originalSize, 0);
        }
    }

    @Override
    public String getSupportedExtension() {
        return ".gz";
    }
}
