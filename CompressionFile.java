import java.io.File;

public class CompressionFile {
    private String filePath;
    private long originalSize;
    private long compressedSize;
    private String fileName;

    public CompressionFile(String filePath) {
        this.filePath = filePath;
        File f = new File(filePath);
        this.fileName = f.getName();
        this.originalSize = f.exists() ? f.length() : 0;
    }

    public double getCompressionRatio() {
        if (originalSize == 0) return 0;
        return ((double)(originalSize - compressedSize) / originalSize) * 100;
    }

    public String getReadableOriginalSize() {
        return formatSize(originalSize);
    }

    public String getReadableCompressedSize() {
        return formatSize(compressedSize);
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024));
    }

    // Getters and Setters
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public long getOriginalSize() { return originalSize; }
    public void setOriginalSize(long originalSize) { this.originalSize = originalSize; }

    public long getCompressedSize() { return compressedSize; }
    public void setCompressedSize(long compressedSize) { this.compressedSize = compressedSize; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}
