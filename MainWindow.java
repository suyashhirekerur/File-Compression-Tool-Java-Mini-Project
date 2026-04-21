import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class MainWindow extends JFrame {

    private CompressionService service;
    private JLabel sourceLabel, destLabel;
    private JProgressBar progressBar;
    private JComboBox<String> algorithmBox;
    private JTextArea statsArea;
    private String selectedSourcePath = "";
    private String selectedDestPath  = "";

    public MainWindow() {
        service = new CompressionService(new ZipCompressor());
        initUI();
    }

    private void initUI() {
        setTitle("File Compression Tool");
        setSize(650, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Compress",   buildCompressPanel());
        tabs.addTab("Decompress", buildDecompressPanel());
        tabs.addTab("About",      buildAboutPanel());

        add(tabs);
        setVisible(true);
    }

    // ── Compress Tab ──────────────────────────────────────────────────
    private JPanel buildCompressPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Algorithm selector
        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topRow.add(new JLabel("Algorithm:"));
        algorithmBox = new JComboBox<>(new String[]{"ZIP", "GZIP", "Documents (PDF/DOCX/PPT/XLS)", "Images (JPG/PNG)"});
        algorithmBox.addActionListener(e -> {
            String algo = (String) algorithmBox.getSelectedItem();
            switch (algo) {
                case "ZIP":
                    service.setCompressor(new ZipCompressor());
                    break;
                case "GZIP":
                    service.setCompressor(new GzipCompressor());
                    break;
                case "Documents (PDF/DOCX/PPT/XLS)":
                    service.setCompressor(new DocumentCompressor());
                    break;
                case "Images (JPG/PNG)":
                    service.setCompressor(new ImageCompressor());
                    break;
                default:
                    service.setCompressor(new ZipCompressor());
            }
        });
        topRow.add(algorithmBox);

        // File selection
        JPanel filePanel = new JPanel(new GridLayout(3, 1, 8, 8));

        sourceLabel = new JLabel("Source file: (not selected)");
        sourceLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        sourceLabel.setForeground(Color.GRAY);

        JButton srcBtn = new JButton("Browse Source File");
        srcBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedSourcePath = fc.getSelectedFile().getAbsolutePath();
                String fileName = fc.getSelectedFile().getName();
                
                // Validate file type
                if (!FileTypeDetector.isFileSupported(selectedSourcePath)) {
                    JOptionPane.showMessageDialog(this, 
                        "Unsupported file type!\n\nSupported formats:\n" + FileTypeDetector.getSupportedFormats(),
                        "File Type Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                sourceLabel.setText("Source: " + fileName + " (" + FileTypeDetector.getFileType(selectedSourcePath) + ")");
                sourceLabel.setForeground(new Color(0, 100, 0));
                
                // Auto-select appropriate compressor
                String recommended = FileTypeDetector.getRecommendedCompressor(selectedSourcePath);
                algorithmBox.setSelectedItem(recommended);
            }
        });

        destLabel = new JLabel("Output folder: (not selected)");
        destLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        destLabel.setForeground(Color.GRAY);

        JButton destBtn = new JButton("Browse Output Folder");
        destBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedDestPath = fc.getSelectedFile().getAbsolutePath();
                destLabel.setText("Output: " + selectedDestPath);
                destLabel.setForeground(new Color(0, 100, 0));
            }
        });

        filePanel.add(srcBtn);
        filePanel.add(sourceLabel);
        filePanel.add(destBtn);
        filePanel.add(destLabel);

        // Progress + stats
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");

        statsArea = new JTextArea(4, 40);
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statsArea.setBorder(BorderFactory.createTitledBorder("Statistics"));

        JButton compressBtn = new JButton("Compress");
        compressBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        compressBtn.setBackground(new Color(30, 90, 200));
        compressBtn.setForeground(Color.WHITE);
        compressBtn.setFocusPainted(false);
        compressBtn.addActionListener(e -> runCompress());

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.add(compressBtn, BorderLayout.NORTH);
        bottom.add(progressBar, BorderLayout.CENTER);
        bottom.add(new JScrollPane(statsArea), BorderLayout.SOUTH);

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(filePanel, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void runCompress() {
        if (selectedSourcePath.isEmpty() || selectedDestPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select source file and output folder.");
            return;
        }

        String ext = service.getSupportedExtension();
        String srcName = new File(selectedSourcePath).getName();
        String destFile = selectedDestPath + File.separator + srcName + ext;

        // SwingWorker keeps UI responsive during compression
        SwingWorker<CompressionResult, Integer> worker = new SwingWorker<>() {
            @Override
            protected CompressionResult doInBackground() {
                progressBar.setIndeterminate(true);
                progressBar.setString("Compressing...");
                return service.compressFile(selectedSourcePath, destFile);
            }
            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setValue(100);
                try {
                    CompressionResult result = get();
                    progressBar.setString(result.isSuccess() ? "Done!" : "Failed");
                    statsArea.setText(result.getSummary());
                    if (result.isSuccess()) {
                        JOptionPane.showMessageDialog(MainWindow.this,
                            "Compressed successfully!\n" + result.getSummary());
                    } else {
                        JOptionPane.showMessageDialog(MainWindow.this,
                            "Compression failed: " + result.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    progressBar.setString("Error");
                    statsArea.setText("Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    // ── Decompress Tab ────────────────────────────────────────────────
    private JPanel buildDecompressPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel srcLbl = new JLabel("Archive: (not selected)");
        srcLbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        srcLbl.setForeground(Color.GRAY);

        JLabel outLbl = new JLabel("Output folder: (not selected)");
        outLbl.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outLbl.setForeground(Color.GRAY);

        final String[] archivePath = {""};
        final String[] outputPath  = {""};

        JButton pickArchive = new JButton("Browse Archive (.zip / .gz / .doc.zip / .img.zip)");
        pickArchive.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                archivePath[0] = fc.getSelectedFile().getAbsolutePath();
                String fileName = fc.getSelectedFile().getName();
                
                // Validate archive type
                if (!FileTypeDetector.isArchiveFile(archivePath[0])) {
                    JOptionPane.showMessageDialog(this, 
                        "Not a valid archive file!\n\nSupported archive formats:\n" + FileTypeDetector.getSupportedFormats(),
                        "Archive Type Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                srcLbl.setText("Archive: " + fileName + " (" + FileTypeDetector.getFileType(archivePath[0]) + ")");
                srcLbl.setForeground(new Color(0, 100, 0));

                // Auto-detect algorithm from extension
                if (archivePath[0].endsWith(".gz")) {
                    service.setCompressor(new GzipCompressor());
                } else if (archivePath[0].endsWith(".doc.zip")) {
                    service.setCompressor(new DocumentCompressor());
                } else if (archivePath[0].endsWith(".img.zip")) {
                    service.setCompressor(new ImageCompressor());
                } else {
                    service.setCompressor(new ZipCompressor());
                }
            }
        });

        JButton pickOutput = new JButton("Browse Output Folder");
        pickOutput.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                outputPath[0] = fc.getSelectedFile().getAbsolutePath();
                outLbl.setText("Output: " + outputPath[0]);
                outLbl.setForeground(new Color(0, 100, 0));
            }
        });

        JButton decompressBtn = new JButton("Decompress");
        decompressBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        decompressBtn.setBackground(new Color(15, 110, 86));
        decompressBtn.setForeground(Color.WHITE);
        decompressBtn.setFocusPainted(false);
        decompressBtn.addActionListener(e -> {
            if (archivePath[0].isEmpty() || outputPath[0].isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select archive and output folder.");
                return;
            }
            CompressionResult result = service.decompressFile(archivePath[0], outputPath[0]);
            JOptionPane.showMessageDialog(this,
                result.isSuccess() ? "Extracted!\n" + result.getSummary() :
                "Extraction failed: " + result.getMessage(),
                result.isSuccess() ? "Success" : "Error",
                result.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });

        JPanel controls = new JPanel(new GridLayout(5, 1, 8, 8));
        controls.add(pickArchive);
        controls.add(srcLbl);
        controls.add(pickOutput);
        controls.add(outLbl);
        controls.add(decompressBtn);

        panel.add(controls, BorderLayout.NORTH);
        return panel;
    }

    // ── About Tab ─────────────────────────────────────────────────────
    private JPanel buildAboutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        JTextArea about = new JTextArea(
            "File Compression Tool\n" +
            "━━━━━━━━━━━━━━━━━━━━\n\n" +
            "SY CSE | Sem IV | Mini Project\n" +
            "AOOC Lab — Java\n\n" +
            "Features:\n" +
            "  • ZIP compression & extraction\n" +
            "  • GZIP compression & extraction\n" +
            "  • Document compression (PDF, DOCX, PPT, PPTX, XLS, XLSX)\n" +
            "  • Image compression (JPG, JPEG, PNG)\n" +
            "  • Background processing (SwingWorker)\n" +
            "  • Compression ratio statistics\n" +
            "  • Smart file type detection\n\n" +
            "Built with: Java Swing, java.util.zip, javax.imageio\n" +
            "IDE: NetBeans / IntelliJ IDEA"
        );
        about.setEditable(false);
        about.setFont(new Font("Monospaced", Font.PLAIN, 13));
        panel.add(new JScrollPane(about), BorderLayout.CENTER);
        return panel;
    }
}
