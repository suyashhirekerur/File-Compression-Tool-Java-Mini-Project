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
    │   └── CompressionService.java       ← Orchestrator (Strategy pattern)
    └── ui/
        └── MainWindow.java               ← Swing GUI (tabbed interface)
```

---

## OOP Concepts Demonstrated

| Concept           | Where                                              |
|-------------------|----------------------------------------------------|
| Interface         | ICompressor                                        |
| Polymorphism      | ZipCompressor & GzipCompressor implement ICompressor |
| Encapsulation     | Model classes with private fields + getters/setters |
| Inheritance       | Both compressors implement same interface          |
| Strategy Pattern  | CompressionService.setCompressor() swaps at runtime |
| Exception Handling | try-catch with IOException in all service methods  |
| Multithreading    | SwingWorker keeps GUI responsive during compression |

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

- ZIP compression and extraction
- GZIP compression and extraction
- Auto-detect algorithm from file extension on decompress
- Background processing using SwingWorker (UI stays responsive)
- Compression ratio and time statistics
- Tabbed GUI (Compress / Decompress / About)
