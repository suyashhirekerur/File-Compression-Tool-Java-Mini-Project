public class CompressionResult {
    private boolean success;
    private String message;
    private long timeTakenMs;
    private long originalSize;
    private long outputSize;

    public CompressionResult(boolean success, String message, long timeTakenMs,
                              long originalSize, long outputSize) {
        this.success = success;
        this.message = message;
        this.timeTakenMs = timeTakenMs;
        this.originalSize = originalSize;
        this.outputSize = outputSize;
    }

    public double getSavingsPercent() {
        if (originalSize == 0) return 0;
        return ((double)(originalSize - outputSize) / originalSize) * 100;
    }

    public String getSummary() {
        if (!success) return "Failed: " + message;
        return String.format(
            "Done in %dms | Saved %.1f%% | %s → %s",
            timeTakenMs, getSavingsPercent(),
            formatSize(originalSize), formatSize(outputSize)
        );
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.2f MB", bytes / (1024.0 * 1024));
    }

    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public long getTimeTakenMs() { return timeTakenMs; }
    public long getOriginalSize() { return originalSize; }
    public long getOutputSize() { return outputSize; }
}
