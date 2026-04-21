import java.io.*;
import java.util.zip.*;

public class DocumentCompressor implements ICompressor {

    private static final int BUFFER_SIZE = 8192;
    private static final String[] SUPPORTED_EXTENSIONS = {".pdf", ".docx", ".ppt", ".pptx", ".xls", ".xlsx"};

    @Override
    public CompressionResult compress(String sourcePath, String destPath) {
        long startTime = System.currentTimeMillis();
        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            return new CompressionResult(false, "Source file not found.", 0, 0, 0);
        }

        if (!isDocumentFile(sourcePath)) {
            return new CompressionResult(false, "Unsupported file type. Only PDF, DOCX, PPT, PPTX, XLS, XLSX files are supported.", 0, 0, 0);
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
            return new CompressionResult(true, "Document compression successful.", timeTaken, originalSize, outputSize);

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
            return new CompressionResult(true, "Document extraction successful.", timeTaken, originalSize, new File(destPath).length());

        } catch (IOException e) {
            return new CompressionResult(false, "Error: " + e.getMessage(), 0, originalSize, 0);
        }
    }

    @Override
    public String getSupportedExtension() {
        return ".doc.zip";
    }

    private boolean isDocumentFile(String filePath) {
        String lowerPath = filePath.toLowerCase();
        for (String ext : SUPPORTED_EXTENSIONS) {
            if (lowerPath.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    public static String[] getSupportedExtensions() {
        return SUPPORTED_EXTENSIONS.clone();
    }
}
