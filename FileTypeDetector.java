import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileTypeDetector {
    
    private static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList(
        ".pdf", ".docx", ".ppt", ".pptx", ".xls", ".xlsx"
    );
    
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png"
    );
    
    private static final List<String> ARCHIVE_EXTENSIONS = Arrays.asList(
        ".zip", ".gz", ".doc.zip", ".img.zip"
    );
    
    public static boolean isDocumentFile(String filePath) {
        return hasExtension(filePath, DOCUMENT_EXTENSIONS);
    }
    
    public static boolean isImageFile(String filePath) {
        return hasExtension(filePath, IMAGE_EXTENSIONS);
    }
    
    public static boolean isArchiveFile(String filePath) {
        return hasExtension(filePath, ARCHIVE_EXTENSIONS);
    }
    
    public static String getFileType(String filePath) {
        String lowerPath = filePath.toLowerCase();
        
        if (isDocumentFile(lowerPath)) {
            return "Document";
        } else if (isImageFile(lowerPath)) {
            return "Image";
        } else if (isArchiveFile(lowerPath)) {
            return "Archive";
        } else {
            return "Unknown";
        }
    }
    
    public static String getRecommendedCompressor(String filePath) {
        String lowerPath = filePath.toLowerCase();
        
        if (isDocumentFile(lowerPath)) {
            return "Documents (PDF/DOCX/PPT/XLS)";
        } else if (isImageFile(lowerPath)) {
            return "Images (JPG/PNG)";
        } else if (lowerPath.endsWith(".gz")) {
            return "GZIP";
        } else {
            return "ZIP";
        }
    }
    
    public static boolean isFileSupported(String filePath) {
        return isDocumentFile(filePath) || isImageFile(filePath) || isArchiveFile(filePath);
    }
    
    public static String getSupportedFormats() {
        StringBuilder sb = new StringBuilder();
        sb.append("Documents: ").append(String.join(", ", DOCUMENT_EXTENSIONS)).append("\n");
        sb.append("Images: ").append(String.join(", ", IMAGE_EXTENSIONS)).append("\n");
        sb.append("Archives: ").append(String.join(", ", ARCHIVE_EXTENSIONS));
        return sb.toString();
    }
    
    private static boolean hasExtension(String filePath, List<String> extensions) {
        String lowerPath = filePath.toLowerCase();
        for (String ext : extensions) {
            if (lowerPath.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
    
    public static String getFileExtension(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex).toLowerCase();
        }
        
        return "";
    }
}
