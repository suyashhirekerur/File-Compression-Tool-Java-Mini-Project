import java.io.*;
import java.util.zip.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageCompressor implements ICompressor {

    private static final int BUFFER_SIZE = 8192;
    private static final String[] SUPPORTED_EXTENSIONS = {".jpg", ".jpeg", ".png"};

    @Override
    public CompressionResult compress(String sourcePath, String destPath) {
        long startTime = System.currentTimeMillis();
        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            return new CompressionResult(false, "Source file not found.", 0, 0, 0);
        }

        if (!isImageFile(sourcePath)) {
            return new CompressionResult(false, "Unsupported file type. Only JPG, JPEG, PNG files are supported.", 0, 0, 0);
        }

        long originalSize = sourceFile.length();

        try {
            // Try to optimize image first
            BufferedImage originalImage = ImageIO.read(sourceFile);
            if (originalImage != null) {
                // For PNG files, try to compress without quality loss
                if (sourcePath.toLowerCase().endsWith(".png")) {
                    File optimizedFile = new File(destPath.replace(".img.zip", "_optimized.png"));
                    if (ImageIO.write(originalImage, "png", optimizedFile)) {
                        long optimizedSize = optimizedFile.length();
                        if (optimizedSize < originalSize) {
                            // If optimization worked, zip the optimized version
                            return createZipArchive(optimizedFile.getAbsolutePath(), destPath, startTime, originalSize);
                        } else {
                            optimizedFile.delete();
                        }
                    }
                }
                
                // For JPEG files, we could apply quality compression but for now use ZIP
            }

            // Fallback to ZIP compression
            return createZipArchive(sourcePath, destPath, startTime, originalSize);

        } catch (IOException e) {
            return new CompressionResult(false, "Error: " + e.getMessage(), 0, originalSize, 0);
        }
    }

    private CompressionResult createZipArchive(String sourcePath, String destPath, long startTime, long originalSize) throws IOException {
        File sourceFile = new File(sourcePath);
        
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
            return new CompressionResult(true, "Image compression successful.", timeTaken, originalSize, outputSize);
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
            return new CompressionResult(true, "Image extraction successful.", timeTaken, originalSize, new File(destPath).length());

        } catch (IOException e) {
            return new CompressionResult(false, "Error: " + e.getMessage(), 0, originalSize, 0);
        }
    }

    @Override
    public String getSupportedExtension() {
        return ".img.zip";
    }

    private boolean isImageFile(String filePath) {
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
