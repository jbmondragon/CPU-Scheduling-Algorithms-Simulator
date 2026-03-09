package gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import src.*;

public class Schedule extends JPanel {

    private Mainframe mainframe;
    private DefaultTableModel tableModel;
    private JTable processTable;
    private JComboBox<String> algoCombo;

    private JPanel quantumPanel;
    private JTextField quantumField;

    // Priority toggle
    private JPanel priorityPanel;
    private JCheckBox higherIsHigherCheck;

    private JPanel deleteButtonsPanel;

    private static final int MIN_ROWS = 3;
    private static final int MAX_ROWS = 20;
    private static final int ICON_SIZE = 26;

    private static final String[] COLUMN_NAMES = {
            "Process ID", "Burst Time", "Arrival Time", "Priority Number"
    };

    private static final String[] ALGORITHMS = {
            "First Come First Serve",
            "Round Robin",
            "SJF (Preemptive)",
            "SJF (Non-preemptive)",
            "Priority (Preemptive)",
            "Priority (Non-preemptive)"
    };

    /** Construct schedule panel bound to parent frame. */
    public Schedule(Mainframe frame) {
        this.mainframe = frame;
        setLayout(new BorderLayout());
        setBackground(Mainframe.BG_DARK);

        ImageIcon addIcon = loadIcon("img/add.png", ICON_SIZE);
        ImageIcon deleteIcon = loadIcon("img/delete.png", ICON_SIZE);
        ImageIcon randomIcon = loadIcon("img/random.png", ICON_SIZE);
        ImageIcon importIcon = loadIcon("img/import.png", ICON_SIZE);

        // =====================================================================

        // =====================================================================
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setBackground(Mainframe.BG_DARK);
        topHeader.setBorder(new EmptyBorder(8, 14, 6, 14));

        JLabel returnLbl = new JLabel("Return");
        returnLbl.setForeground(Mainframe.TEXT_LIGHT);
        returnLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        returnLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        returnLbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainframe.showCard("MENU");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                returnLbl.setText("<html><u>Return</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                returnLbl.setText("Return");
            }
        });

        JLabel aisaLbl = new JLabel("AISA", SwingConstants.CENTER);
        aisaLbl.setForeground(Mainframe.TEXT_LIGHT);
        aisaLbl.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel spacer = new JLabel("Return");
        spacer.setFont(returnLbl.getFont());
        spacer.setForeground(Mainframe.BG_DARK);
        spacer.setVisible(false);
        spacer.setPreferredSize(returnLbl.getPreferredSize());

        topHeader.add(returnLbl, BorderLayout.WEST);
        topHeader.add(aisaLbl, BorderLayout.CENTER);
        topHeader.add(spacer, BorderLayout.EAST);
        add(topHeader, BorderLayout.NORTH);

        // =====================================================================

        // =====================================================================
        JPanel body = new JPanel(new BorderLayout(12, 0));
        body.setBackground(Mainframe.BG_DARK);
        body.setBorder(new EmptyBorder(4, 8, 8, 8));

        JPanel leftCard = new JPanel(new BorderLayout());
        leftCard.setBackground(new Color(232, 232, 232));
        leftCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185, 185, 185), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 14, 0)));

        JPanel procHeader = new JPanel(new BorderLayout());
        procHeader.setBackground(Color.BLACK);
        procHeader.setBorder(new EmptyBorder(11, 16, 11, 16));

        JPanel procTitleBox = new JPanel();
        procTitleBox.setLayout(new BoxLayout(procTitleBox, BoxLayout.Y_AXIS));
        procTitleBox.setBackground(Color.BLACK);

        JLabel procTitle = new JLabel("Processes");
        procTitle.setFont(new Font("Arial", Font.BOLD, 16));
        procTitle.setForeground(Color.WHITE);

        JLabel procSub = new JLabel("Add a process for simulation");
        procSub.setFont(new Font("Arial", Font.PLAIN, 11));
        procSub.setForeground(new Color(175, 175, 175));

        procTitleBox.add(procTitle);
        procTitleBox.add(procSub);
        procHeader.add(procTitleBox, BorderLayout.WEST);
        leftCard.add(procHeader, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col != 0;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                return String.class;
            }
        };

        processTable = new JTable(tableModel);
        processTable.setRowHeight(27);
        processTable.setBackground(Color.WHITE);
        processTable.setShowGrid(true);
        processTable.setGridColor(new Color(210, 210, 210));
        processTable.setSelectionBackground(new Color(200, 220, 245));
        processTable.setFont(new Font("Arial", Font.PLAIN, 12));
        processTable.setFillsViewportHeight(false);

        processTable.getTableHeader().setBackground(new Color(230, 230, 230));
        processTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        processTable.getTableHeader().setReorderingAllowed(false);
        processTable.getTableHeader().setPreferredSize(new Dimension(0, 28));

        for (int i = 0; i < 4; i++) {
            processTable.getColumnModel().getColumn(i).setPreferredWidth(110);
        }

        processTable.getColumnModel().getColumn(0).setCellEditor(
                new ValidatedCellEditor(ValidatedCellEditor.Mode.PROCESS_ID));
        processTable.getColumnModel().getColumn(1).setCellEditor(
                new ValidatedCellEditor(ValidatedCellEditor.Mode.BURST));
        processTable.getColumnModel().getColumn(2).setCellEditor(
                new ValidatedCellEditor(ValidatedCellEditor.Mode.ARRIVAL));
        processTable.getColumnModel().getColumn(3).setCellEditor(
                new ValidatedCellEditor(ValidatedCellEditor.Mode.PRIORITY));

        JScrollPane tableScroll = new JScrollPane(processTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(185, 185, 185), 1));
        tableScroll.getViewport().setBackground(Color.WHITE);

        JPanel deleteStrip = new JPanel(new BorderLayout(0, 0));
        deleteStrip.setBackground(new Color(232, 232, 232));
        deleteStrip.setPreferredSize(new Dimension(46, 0));

        JPanel deleteLblBox = new JPanel();
        deleteLblBox.setLayout(new BoxLayout(deleteLblBox, BoxLayout.Y_AXIS));
        deleteLblBox.setBackground(new Color(232, 232, 232));
        deleteLblBox.setPreferredSize(new Dimension(46, 28));
        deleteLblBox.setBorder(new EmptyBorder(2, 0, 2, 0));

        JLabel delLbl1 = new JLabel("Delete", SwingConstants.CENTER);
        delLbl1.setFont(new Font("Arial", Font.PLAIN, 9));
        delLbl1.setForeground(Color.DARK_GRAY);
        delLbl1.setAlignmentX(Component.CENTER_ALIGNMENT);
        delLbl1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 13));

        JLabel delLbl2 = new JLabel("Entry", SwingConstants.CENTER);
        delLbl2.setFont(new Font("Arial", Font.PLAIN, 9));
        delLbl2.setForeground(Color.DARK_GRAY);
        delLbl2.setAlignmentX(Component.CENTER_ALIGNMENT);
        delLbl2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 13));

        deleteLblBox.add(delLbl1);
        deleteLblBox.add(delLbl2);

        deleteButtonsPanel = new JPanel();
        deleteButtonsPanel.setLayout(new BoxLayout(deleteButtonsPanel, BoxLayout.Y_AXIS));
        deleteButtonsPanel.setBackground(new Color(232, 232, 232));

        deleteStrip.add(deleteLblBox, BorderLayout.NORTH);
        deleteStrip.add(deleteButtonsPanel, BorderLayout.CENTER);

        JPanel tableRow = new JPanel(new BorderLayout(5, 0));
        tableRow.setBackground(new Color(232, 232, 232));
        tableRow.setBorder(new EmptyBorder(10, 12, 0, 12));
        tableRow.add(tableScroll, BorderLayout.CENTER);
        tableRow.add(deleteStrip, BorderLayout.EAST);

        leftCard.add(tableRow, BorderLayout.CENTER);

        for (int i = 1; i <= MIN_ROWS; i++) {
            tableModel.addRow(new Object[] { "", "", "", "" });
        }
        assessPID();
        refreshDeleteButtons(deleteIcon);

        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.INSERT ||
                    e.getType() == TableModelEvent.DELETE) {
                refreshDeleteButtons(deleteIcon);
            }
        });

        // =================================================================
        // RIGHT — controls panel
        // =================================================================
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Mainframe.BG_DARK);
        rightPanel.setPreferredSize(new Dimension(190, 0));

        // Full-width black logo block at top
        JPanel logoBlock = new JPanel();
        logoBlock.setBackground(Color.BLACK);
        logoBlock.setMinimumSize(new Dimension(190, 68));
        logoBlock.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        logoBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(logoBlock);
        rightPanel.add(Box.createVerticalStrut(12));

        // ---- Action buttons with image icons on WHITE backgrounds ----
        rightPanel.add(makeActionRow(addIcon, "Add Process", e -> addRow()));
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(makeActionRow(deleteIcon, "Clear All", e -> clearAll()));
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(makeActionRow(randomIcon, "Random", e -> randomFill()));
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(makeActionRow(importIcon, "Import", e -> importFile()));
        rightPanel.add(Box.createVerticalStrut(14));

        // Algorithm selector dark box
        JPanel algoBox = new JPanel();
        algoBox.setLayout(new BoxLayout(algoBox, BoxLayout.Y_AXIS));
        algoBox.setBackground(new Color(50, 50, 50));
        algoBox.setBorder(new EmptyBorder(10, 10, 12, 10));
        algoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        algoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel algoLbl = new JLabel("<html>Select A Scheduling Algorithm To Simulate:</html>");
        algoLbl.setForeground(Color.WHITE);
        algoLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        algoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        algoCombo = new JComboBox<>(ALGORITHMS);
        algoCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        algoCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        algoCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Quantum Time (Round Robin only)
        quantumPanel = new JPanel(new BorderLayout(5, 0));
        quantumPanel.setBackground(new Color(50, 50, 50));
        quantumPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quantumPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel quantumLbl = new JLabel("Quantum Time");
        quantumLbl.setForeground(Color.WHITE);
        quantumLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        quantumField = new JTextField();
        quantumField.setFont(new Font("Arial", Font.PLAIN, 12));
        quantumField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                if (!Character.isDigit(c) && !Character.isISOControl(c)) {
                    e.consume();
                }
            }
        });
        quantumPanel.add(quantumLbl, BorderLayout.WEST);
        quantumPanel.add(quantumField, BorderLayout.CENTER);
        quantumPanel.setVisible(false);

        // Priority toggle (Priority algorithms only)
        priorityPanel = new JPanel(new BorderLayout(4, 0));
        priorityPanel.setBackground(new Color(50, 50, 50));
        priorityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priorityPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel prioLbl = new JLabel("<html>Higher # = Higher Priority</html>");
        prioLbl.setForeground(Color.WHITE);
        prioLbl.setFont(new Font("Arial", Font.PLAIN, 10));
        higherIsHigherCheck = new JCheckBox();
        higherIsHigherCheck.setBackground(new Color(50, 50, 50));
        priorityPanel.add(prioLbl, BorderLayout.WEST);
        priorityPanel.add(higherIsHigherCheck, BorderLayout.EAST);
        priorityPanel.setVisible(false);

        algoCombo.addActionListener(e -> updateAlgoExtras());

        algoBox.add(algoLbl);
        algoBox.add(Box.createVerticalStrut(7));
        algoBox.add(algoCombo);
        algoBox.add(Box.createVerticalStrut(6));
        algoBox.add(quantumPanel);
        algoBox.add(priorityPanel);

        rightPanel.add(algoBox);
        rightPanel.add(Box.createVerticalGlue());

        // Submit button
        JButton submitBtn = new JButton("Submit");
        submitBtn.setBackground(Color.BLACK);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Arial", Font.BOLD, 15));
        submitBtn.setFocusPainted(false);
        submitBtn.setOpaque(true);
        submitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        submitBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
                BorderFactory.createEmptyBorder(8, 0, 8, 0)));
        submitBtn.addActionListener(e -> runSimulation());
        rightPanel.add(submitBtn);

        // =================================================================
        body.add(leftCard, BorderLayout.CENTER);
        body.add(rightPanel, BorderLayout.EAST);
        add(body, BorderLayout.CENTER);
    }

    // =========================================================================
    // Rebuild delete button strip — called every time rows change
    // =========================================================================
    /** Rebuilds the vertical strip of delete buttons matching table rows. */
    private void refreshDeleteButtons(ImageIcon deleteIcon) {
        deleteButtonsPanel.removeAll();
        int rowCount = tableModel.getRowCount();
        boolean canDelete = rowCount > MIN_ROWS;

        for (int i = 0; i < rowCount; i++) {
            final int row = i;

            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(30, 27));
            btn.setMaximumSize(new Dimension(30, 27));
            btn.setMinimumSize(new Dimension(30, 27));

            if (deleteIcon != null) {
                btn.setIcon(deleteIcon);
                btn.setText("");
            } else {
                btn.setText("\u2715");
                btn.setFont(new Font("Arial", Font.BOLD, 10));
            }

            // White background so black icon is clearly visible; gray when disabled
            btn.setBackground(canDelete ? Color.WHITE : new Color(180, 180, 180));
            btn.setFocusPainted(false);
            btn.setBorderPainted(canDelete);
            btn.setBorder(canDelete
                    ? BorderFactory.createLineBorder(new Color(160, 160, 160), 1)
                    : BorderFactory.createEmptyBorder());
            btn.setOpaque(true);
            btn.setEnabled(canDelete);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setToolTipText(canDelete ? "Delete this row"
                    : "Minimum " + MIN_ROWS + " rows required");

            btn.addActionListener(e -> {
                if (tableModel.getRowCount() > MIN_ROWS) {
                    if (processTable.isEditing()) {
                        processTable.getCellEditor().stopCellEditing();
                    }
                    tableModel.removeRow(row);
                    assessPID();
                }
            });

            deleteButtonsPanel.add(btn);
        }
        deleteButtonsPanel.revalidate();
        deleteButtonsPanel.repaint();
    }

    // =========================================================================
    // Show / hide algo-specific extras
    // =========================================================================
    /** Show or hide UI controls that depend on the selected algorithm. */
    private void updateAlgoExtras() {
        String sel = (String) algoCombo.getSelectedItem();
        quantumPanel.setVisible("Round Robin".equals(sel));
        boolean isPriority = sel != null && sel.startsWith("Priority");
        priorityPanel.setVisible(isPriority);
        revalidate();
        repaint();
    }

    // =========================================================================
    // Action row builder — WHITE square button with image icon + label
    // =========================================================================
    /** Create a single row containing an icon button and a label. */
    private JPanel makeActionRow(ImageIcon icon, String label, ActionListener action) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setBackground(Mainframe.BG_DARK);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton iconBtn = new JButton();
        iconBtn.setPreferredSize(new Dimension(44, 44));
        iconBtn.setBackground(Color.WHITE);
        iconBtn.setFocusPainted(false);
        iconBtn.setOpaque(true);
        iconBtn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        if (icon != null) {
            iconBtn.setIcon(icon);
        } else {
            iconBtn.setText(label.substring(0, 1));
            iconBtn.setFont(new Font("Arial", Font.BOLD, 16));
        }

        iconBtn.addActionListener(action);

        JLabel lbl = new JLabel(label);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));

        row.add(iconBtn);
        row.add(lbl);
        return row;
    }

    // =========================================================================
    // Icon loader — scales image to targetSize x targetSize
    // Tries classpath resource first, then filesystem fallback
    // =========================================================================
    /** Load and scale an image resource, returning an ImageIcon or null. */
    private ImageIcon loadIcon(String path, int targetSize) {
        try {
            URL url = getClass().getClassLoader().getResource(path);
            BufferedImage img;
            if (url != null) {
                img = ImageIO.read(url);
            } else {

                File f = new File(path);
                if (!f.exists())
                    return null;
                img = ImageIO.read(f);
            }
            if (img == null)
                return null;
            Image scaled = img.getScaledInstance(targetSize, targetSize, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            System.err.println("Could not load icon: " + path + " — " + ex.getMessage());
            return null;
        }
    }

    // =========================================================================
    // Button actions
    // =========================================================================
    /** Add an empty process row (if under max limit). */
    private void addRow() {
        int n = tableModel.getRowCount();
        if (n >= MAX_ROWS) {
            JOptionPane.showMessageDialog(this,
                    "Maximum of " + MAX_ROWS + " processes allowed.",
                    "Limit Reached", JOptionPane.WARNING_MESSAGE);
            return;
        }
        tableModel.addRow(new Object[] { "", "", "", "" });
        assessPID();
    }

    /** Clear all rows and repopulate minimum default rows. */
    private void clearAll() {
        tableModel.setRowCount(0);
        for (int i = 1; i <= MIN_ROWS; i++) {
            tableModel.addRow(new Object[] { "", "", "", "" });
        }
        assessPID();
    }

    /** Populate table with random process data for testing. */
    private void randomFill() {
        Random rand = new Random();
        int n = MIN_ROWS + rand.nextInt(MAX_ROWS - MIN_ROWS + 1);
        tableModel.setRowCount(0);

        java.util.List<Integer> priorities = new java.util.ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            priorities.add(i);
        }
        java.util.Collections.shuffle(priorities);

        for (int i = 0; i < n; i++) {
            tableModel.addRow(new Object[] {
                    "P" + (i + 1),
                    1 + rand.nextInt(30),
                    rand.nextInt(16),
                    priorities.get(i)
            });
        }
        assessPID();
    }

    /** Import processes from a selected file (.txt, .csv, .xlsx). */
    private void importFile() {
        JFileChooser fc = new JFileChooser();
        // start in the workspace dataset folder if it exists
        File dataDir = new File("dataset");
        if (dataDir.exists() && dataDir.isDirectory()) {
            fc.setCurrentDirectory(dataDir);
        }
        fc.setDialogTitle("Import Process List (.txt/.csv/.xlsx)");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Text/CSV/XLSX files", "txt", "csv", "xlsx"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        File file = fc.getSelectedFile();
        String name = file.getName().toLowerCase();
        try {
            tableModel.setRowCount(0);
            if (name.endsWith(".xlsx")) {
                for (String[] row : readXlsx(file)) {
                    if (row.length >= 4) {
                        tableModel.addRow(new Object[] { row[0], row[1], row[2], row[3] });
                    } else if (row.length == 3) {
                        tableModel.addRow(new Object[] { row[0], row[1], row[2], "" });
                    }
                }
            } else {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#"))
                            continue;
                        String[] p = line.split("[,\\s]+");
                        if (p.length >= 4) {
                            tableModel.addRow(new Object[] { p[0], p[1], p[2], p[3] });
                        }
                    }
                }
            }

            while (tableModel.getRowCount() < MIN_ROWS) {
                tableModel.addRow(new Object[] { "", "", "", "" });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not read file:\n" + ex.getMessage(),
                    "Import Error", JOptionPane.ERROR_MESSAGE);
        }
        assessPID();
    }

    /** Validate table data, create jobs, run selected scheduling algorithm. */
    private void runSimulation() {
        if (processTable.isEditing()) {
            processTable.getCellEditor().stopCellEditing();
        }

        int rows = tableModel.getRowCount();
        java.util.Set<String> usedIDs = new java.util.HashSet<>();
        java.util.Set<Integer> usedPriority = new java.util.HashSet<>();

        String[] colNames = { "Process ID", "Burst Time", "Arrival Time", "Priority Number" };
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < 4; c++) {
                if (trim(tableModel.getValueAt(r, c)).isEmpty()) {
                    error("Row " + (r + 1) + ", " + colNames[c]
                            + ": No cells should be empty. Please fill in all fields before submitting.");
                    return;
                }
            }
        }

        for (int r = 0; r < rows; r++) {
            String rowLabel = "Row " + (r + 1) + ": ";

            String pid = trim(tableModel.getValueAt(r, 0));
            if (!usedIDs.add(pid)) {
                error(rowLabel + "Process ID \"" + pid + "\" is duplicated.");
                return;
            }

            int burst = parseInt(tableModel.getValueAt(r, 1));
            if (burst < 1 || burst > 30) {
                error(rowLabel + "Burst Time must be a number between 1 and 30.");
                return;
            }

            int arrival = parseInt(tableModel.getValueAt(r, 2));
            if (arrival < 0 || arrival > 30) {
                error(rowLabel + "Arrival Time must be a number between 0 and 30.");
                return;
            }

            int priority = parseInt(tableModel.getValueAt(r, 3));
            if (priority < 1 || priority > 20) {
                error(rowLabel + "Priority Number must be between 1 and 20.");
                return;
            }
            if (!usedPriority.add(priority)) {
                error(rowLabel + "Priority Number " + priority + " is already used by another process.");
                return;
            }
        }

        // ---- Validate quantum if Round Robin ----
        String algoName = (String) algoCombo.getSelectedItem();
        int quantumTime = 0;
        if ("Round Robin".equals(algoName)) {
            quantumTime = parseInt(quantumField.getText());
            if (quantumTime < 1 || quantumTime > 10) {
                error("Quantum Time must be between 1 and 10.");
                return;
            }
        }

        // ---- Create Job objects from table data ----
        java.util.List<Job> jobs = new java.util.ArrayList<>();
        for (int r = 0; r < rows; r++) {
            int burst = parseInt(tableModel.getValueAt(r, 1));
            int arrival = parseInt(tableModel.getValueAt(r, 2));
            int priority = parseInt(tableModel.getValueAt(r, 3));
            Job job = new Job(burst, arrival, priority);
            job.processID = "P" + (r + 1);
            jobs.add(job);
        }

        // ---- Create scheduler and run simulation ----
        Scheduler scheduler = createScheduler(algoName, quantumTime);
        if (scheduler == null) {
            error("Could not create scheduler for algorithm: " + algoName);
            return;
        }

        ScheduleResult result = scheduler.schedule(jobs);

        // ---- Display results ----
        Result resultPanel = mainframe.getResultPanel();
        resultPanel.displayResult(algoName, result, quantumTime);

        mainframe.showCard("RESULT");
    }

    // =========================================================================
    // Create scheduler based on algorithm name
    // =========================================================================
    /** Factory that returns a Scheduler implementation based on algorithm name. */
    private Scheduler createScheduler(String algorithmName, int quantumTime) {
        return switch (algorithmName) {
            case "First Come First Serve" -> new FCFS();
            case "Round Robin" -> new RoundRobin(quantumTime);
            case "SJF (Preemptive)" -> new SJFPreemptive();
            case "SJF (Non-preemptive)" -> new SJFNonPreemptive();
            case "Priority (Preemptive)" -> new PriorityPreemptive(higherIsHigherCheck.isSelected());
            case "Priority (Non-preemptive)" -> new PriorityNonPreemptive(higherIsHigherCheck.isSelected());
            default -> null;
        };
    }

    // =========================================================================
    // Validation helpers
    // =========================================================================
    /** Utility: return trimmed string representation or empty. */
    private String trim(Object val) {
        return val == null ? "" : val.toString().trim();
    }

    /** Parse integer safely, returning MIN_VALUE on failure. */
    private int parseInt(Object val) {
        try {
            return Integer.parseInt(trim(val));
        } catch (NumberFormatException e) {
            return Integer.MIN_VALUE;
        }
    }

    /** Show validation error message in dialog. */
    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }

    private void assessPID() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt("P" + (i + 1), i, 0);
        }
    }

    /** Read rows from an XLSX file's first worksheet. */
    private java.util.List<String[]> readXlsx(File file) throws Exception {
        java.util.List<String[]> rows = new java.util.ArrayList<>();
        try (ZipFile zip = new ZipFile(file)) {
            // load shared strings table if present
            java.util.List<String> shared = new java.util.ArrayList<>();
            ZipEntry sst = zip.getEntry("xl/sharedStrings.xml");
            if (sst != null) {
                Document sstDoc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().parse(zip.getInputStream(sst));
                NodeList siList = sstDoc.getElementsByTagName("si");
                for (int i = 0; i < siList.getLength(); i++) {
                    shared.add(siList.item(i).getTextContent());
                }
            }

            ZipEntry sheet = zip.getEntry("xl/worksheets/sheet1.xml");
            if (sheet != null) {
                Document sheetDoc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().parse(zip.getInputStream(sheet));
                NodeList rowList = sheetDoc.getElementsByTagName("row");
                for (int i = 0; i < rowList.getLength(); i++) {
                    Element row = (Element) rowList.item(i);
                    NodeList cellList = row.getElementsByTagName("c");
                    java.util.List<String> rowVals = new java.util.ArrayList<>();
                    for (int j = 0; j < cellList.getLength(); j++) {
                        Element c = (Element) cellList.item(j);
                        String type = c.getAttribute("t");
                        String v = "";
                        NodeList vnodes = c.getElementsByTagName("v");
                        if (vnodes.getLength() > 0) {
                            v = vnodes.item(0).getTextContent();
                            if ("s".equals(type)) {
                                try {
                                    int idx = Integer.parseInt(v);
                                    if (idx < shared.size()) {
                                        v = shared.get(idx);
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                        rowVals.add(v);
                    }
                    rows.add(rowVals.toArray(new String[0]));
                }
            }
        }
        return rows;
    }

    // =========================================================================
    // ValidatedCellEditor — enforces range + type rules per column
    // =========================================================================
    private class ValidatedCellEditor extends DefaultCellEditor {

        enum Mode {
            PROCESS_ID, BURST, ARRIVAL, PRIORITY
        }

        private final Mode mode;
        private final JTextField field;
        private int editingRow;

        ValidatedCellEditor(Mode mode) {
            super(new JTextField());
            this.mode = mode;
            this.field = (JTextField) getComponent();

            if (mode != Mode.PROCESS_ID) {
                field.addKeyListener(new KeyAdapter() {
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();

                        if (!Character.isDigit(c) && !Character.isISOControl(c)) {
                            e.consume();
                        }
                    }
                });
            }

            field.setFont(new Font("Arial", Font.PLAIN, 12));
            field.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 200), 1));
            setClickCountToStart(1);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            editingRow = row;
            field.setText(value == null ? "" : value.toString());
            field.setBackground(Color.WHITE);
            return field;
        }

        @Override
        public boolean stopCellEditing() {
            String raw = field.getText().trim();

            switch (mode) {
                case PROCESS_ID:
                    if (raw.isEmpty()) {
                        flash();
                        return false;
                    }
                    // Check uniqueness across other rows
                    for (int r = 0; r < tableModel.getRowCount(); r++) {
                        if (r == editingRow)
                            continue;
                        String other = trim(tableModel.getValueAt(r, 0));
                        if (raw.equalsIgnoreCase(other)) {
                            flash();
                            return false;
                        }
                    }
                    break;

                case BURST:
                    if (!inRange(raw, 1, 30)) {
                        flash();
                        return false;
                    }
                    break;

                case ARRIVAL:
                    if (!inRange(raw, 0, 30)) {
                        flash();
                        return false;
                    }
                    break;

                case PRIORITY:
                    if (!inRange(raw, 1, 20)) {
                        flash();
                        return false;
                    }
                    // Check duplicate priority across other rows
                    try {
                        int val = Integer.parseInt(raw);
                        for (int r = 0; r < tableModel.getRowCount(); r++) {
                            if (r == editingRow)
                                continue;
                            int other = parseInt(tableModel.getValueAt(r, 3));
                            if (val == other) {
                                flash();
                                return false;
                            }
                        }
                    } catch (NumberFormatException ignored) {
                    }
                    break;
            }

            field.setBackground(Color.WHITE);
            return super.stopCellEditing();
        }

        @Override
        public Object getCellEditorValue() {
            return field.getText().trim();
        }

        /** Flash cell red to signal invalid input. */
        private void flash() {
            field.setBackground(new Color(255, 200, 200));
            field.selectAll();
            showHint();
        }

        private void showHint() {
            String hint;
            switch (mode) {
                case PROCESS_ID:
                    hint = "Process ID must be unique and non-empty.";
                    break;
                case BURST:
                    hint = "Burst Time must be a number between 1 and 30.";
                    break;
                case ARRIVAL:
                    hint = "Arrival Time must be a number between 0 and 30.";
                    break;
                case PRIORITY:
                    hint = "Priority Number must be between 1 and 20 with no duplicates.";
                    break;
                default:
                    hint = "Invalid value.";
            }
            JOptionPane.showMessageDialog(Schedule.this, hint, "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);
        }

        private boolean inRange(String raw, int min, int max) {
            try {
                int v = Integer.parseInt(raw);
                return v >= min && v <= max;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

}
