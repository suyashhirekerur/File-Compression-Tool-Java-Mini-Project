

public class CompressionService {

    private ICompressor compressor;

    public CompressionService(ICompressor compressor) {
        this.compressor = compressor;
    }

    // Strategy pattern — swap compressor at runtime
    public void setCompressor(ICompressor compressor) {
        this.compressor = compressor;
    }

    public CompressionResult compressFile(String sourcePath, String destPath) {
        if (sourcePath == null || sourcePath.isEmpty())
            return new CompressionResult(false, "No source file specified.", 0, 0, 0);
        if (destPath == null || destPath.isEmpty())
            return new CompressionResult(false, "No destination path specified.", 0, 0, 0);

        return compressor.compress(sourcePath, destPath);
    }

    public CompressionResult decompressFile(String sourcePath, String destPath) {
        if (sourcePath == null || sourcePath.isEmpty())
            return new CompressionResult(false, "No source file specified.", 0, 0, 0);

        return compressor.decompress(sourcePath, destPath);
    }

    public String getSupportedExtension() {
        return compressor.getSupportedExtension();
    }
}
