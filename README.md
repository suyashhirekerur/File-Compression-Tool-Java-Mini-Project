# File Compression Tool — Java Mini Project
## SY CSE | Sem IV | AOOC Lab | 2025-26

---

## Project Structure

```
FileCompressionTool/
└── src/
    ├── Main.java                          ← Entry point
    ├── interfaces/
    │   └── ICompressor.java              ← Interface (compress / decompress)
    ├── model/
    │   ├── CompressionFile.java          ← File metadata model
    │   └── CompressionResult.java        ← Result + statistics model
    ├── service/
    │   ├── ZipCompressor.java            ← ZIP implementation
    │   ├── GzipCompressor.java           ← GZIP implementation
    │   ├── DocumentCompressor.java       ← Document files (PDF, DOCX, PPT, XLS)
    │   ├── ImageCompressor.java          ← Image files (JPG, JPEG, PNG)
    │   ├── CompressionService.java       ← Orchestrator (Strategy pattern)
    │   └── FileTypeDetector.java         ← Smart file type detection
    └── ui/
        └── MainWindow.java               ← Swing GUI (tabbed interface)
```

---

## OOP Concepts Demonstrated

| Concept           | Where                                                                              |
|-------------------|------------------------------------------------------------------------------------|
| Interface         | ICompressor                                                                         |
| Polymorphism      | All compressor classes (Zip, GZIP, Document, Image) implement ICompressor           |
| Encapsulation     | Model classes with private fields + getters/setters                                |
| Inheritance       | All compressors implement same interface                                            |
| Strategy Pattern  | CompressionService.setCompressor() swaps at runtime                                |
| Factory Pattern   | FileTypeDetector.getRecommendedCompressor() suggests appropriate compressor           |
| Exception Handling | try-catch with IOException in all service methods                                  |
| Multithreading    | SwingWorker keeps GUI responsive during compression                                 |
| File Type Detection| FileTypeDetector class with smart validation and auto-detection                     |

---

## How to Run in NetBeans

1. Create a new **Java Application** project in NetBeans
2. Copy all `.java` files into the `src/` folder (maintaining package names)
3. Set `Main.java` as the main class
4. Click **Run (F6)**

---

## How to Run in IntelliJ IDEA

1. File > New Project > Java
2. Copy source files maintaining package structure
3. Right-click `Main.java` > Run 'Main'

---

## Features

### Core Compression
- ZIP compression and extraction
- GZIP compression and extraction
- Document compression (PDF, DOCX, PPT, PPTX, XLS, XLSX)
- Image compression (JPG, JPEG, PNG) with optimization support

### Smart Features
- Auto-detect algorithm from file extension on decompress
- Smart file type detection and validation
- Automatic compressor selection based on file type
- File type validation with user-friendly error messages

### User Experience
- Background processing using SwingWorker (UI stays responsive)
- Compression ratio and time statistics
- Tabbed GUI (Compress / Decompress / About)
- Enhanced file browser with type indicators

### Supported Formats
- **Documents**: PDF, DOCX, PPT, PPTX, XLS, XLSX
- **Images**: JPG, JPEG, PNG (with optimization)
- **Archives**: ZIP, GZIP, DOC.ZIP, IMG.ZIP
