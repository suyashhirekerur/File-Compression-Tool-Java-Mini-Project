

public interface ICompressor {
    CompressionResult compress(String sourcePath, String destPath);
    CompressionResult decompress(String sourcePath, String destPath);
    String getSupportedExtension();
}
