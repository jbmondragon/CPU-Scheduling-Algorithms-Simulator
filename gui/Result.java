package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import src.*;

public class Result extends JPanel {

    private final Mainframe mainframe;
    private JTable table;
    private DefaultTableModel model;
    private JLabel algoNameLbl;
    private GanttChartPanel ganttPanel;
    private JLabel avgWaitingTimeLbl;
    private JLabel avgTurnaroundTimeLbl;
    private JLabel quantumTimeLbl;

    public Result(Mainframe frame) {
        this.mainframe = frame;

        setLayout(new BorderLayout());
        setBackground(Mainframe.BG_DARK);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    // =========================
    // HEADER
    // =========================
    private JPanel createHeader() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Mainframe.BG_LIGHT_GRAY);

        JLabel mainPageLabel = new JLabel(" Simulation Output");
        headerPanel.add(mainPageLabel, BorderLayout.WEST);

        return headerPanel;
    }

    // =========================
    // MAIN WRAPPER
    // =========================
    private JPanel createMainContent() {

        JPanel whiteWrapper = new JPanel(new BorderLayout());
        whiteWrapper.setBackground(Color.WHITE);
        whiteWrapper.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel contentBody = createContentBody();

        ganttPanel = new GanttChartPanel();

        // Wrap gantt chart in scroll pane for horizontal scrolling
        JScrollPane ganttScroll = new JScrollPane(ganttPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        ganttScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        whiteWrapper.add(contentBody, BorderLayout.CENTER);
        whiteWrapper.add(ganttScroll, BorderLayout.SOUTH);

        return whiteWrapper;
    }

    // =========================
    // CONTENT BODY
    // =========================
    private JPanel createContentBody() {

        JPanel contentBody = new JPanel(new BorderLayout());
        contentBody.setBackground(Mainframe.BG_LIGHT_GRAY);
        contentBody.setBorder(new EmptyBorder(20, 20, 0, 20));

        contentBody.add(createContentHeader(), BorderLayout.NORTH);
        contentBody.add(createTableSection(), BorderLayout.CENTER);
        contentBody.add(createStatsPanel(), BorderLayout.SOUTH);

        return contentBody;
    }

    // =========================
    // CONTENT HEADER
    // =========================
    private JPanel createContentHeader() {

        JPanel contentHeader = new JPanel(new BorderLayout());
        contentHeader.setBackground(Mainframe.BG_LIGHT_GRAY);

        algoNameLbl = new JLabel("Algorithm Name");
        algoNameLbl.setFont(new Font("Arial", Font.BOLD, 14));

        JButton backButton = new JButton("<-");
        backButton.setBackground(Mainframe.BG_DARK);
        backButton.setForeground(Mainframe.TEXT_LIGHT);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainframe.showCard("SCHEDULE"));

        contentHeader.add(algoNameLbl, BorderLayout.WEST);
        contentHeader.add(backButton, BorderLayout.EAST);

        return contentHeader;
    }

    // =========================
    // TABLE SECTION
    // =========================
    private JPanel createTableSection() {

        String[] columnNames = {
                "Process ID",
                "Burst Time",
                "Arrival Time",
                "Priority",
                "Waiting Time",
                "Turnaround Time"
        };

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(200, 215, 200));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setBackground(Mainframe.BG_LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(Mainframe.BG_LIGHT_GRAY);
        tableWrapper.setBorder(new EmptyBorder(20, 0, 20, 0));
        tableWrapper.add(scrollPane);

        return tableWrapper;
    }

    // =========================
    // DISPLAY RESULTS
    // =========================

    public void displayResult(String algorithmName, ScheduleResult result) {

        algoNameLbl.setText(algorithmName);
        model.setRowCount(0);

        for (Job job : result.jobs) {

            model.addRow(new Object[] {
                    "P" + job.processID,
                    job.burstTime,
                    job.arrivalTime,
                    job.priorityNumber,
                    job.waitingTime,
                    job.turnaroundTime
            });

        }

        ganttPanel.setGanttData(result.ganttChart);

        // Update stats
        avgWaitingTimeLbl.setText(String.format("Average Waiting Time: %.2f", result.averageWaitingTime));
        avgTurnaroundTimeLbl.setText(String.format("Average Turnaround Time: %.2f", result.averageTurnaroundTime));
    }

    // =========================
    // STATS PANEL
    // =========================
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(Mainframe.BG_LIGHT_GRAY);
        statsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        avgWaitingTimeLbl = new JLabel("Average Waiting Time: 0.00");
        avgWaitingTimeLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        avgTurnaroundTimeLbl = new JLabel("Average Turnaround Time: 0.00");
        avgTurnaroundTimeLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        quantumTimeLbl = new JLabel("Quantum Time: -");
        quantumTimeLbl.setFont(new Font("Arial", Font.PLAIN, 12));

        statsPanel.add(avgWaitingTimeLbl);
        statsPanel.add(avgTurnaroundTimeLbl);
        statsPanel.add(quantumTimeLbl);

        return statsPanel;
    }

    // =========================
    // GANTT CHART PANEL
    // =========================
    private class GanttChartPanel extends JPanel {

        private List<Integer> gantt;
        private int currentTime = 0;
        private final Timer animationTimer;
        private boolean isRunning = false;
        private JButton playPauseBtn;
        private JButton resetBtn;
        private JLabel timerLabel;
        private final JPanel controlPanel;
        private JPanel animationPanel;

        public GanttChartPanel() {
            setLayout(new BorderLayout());
            setBackground(Mainframe.BG_DARK);
            setPreferredSize(new Dimension(0, 90));

            // Control panel with buttons
            controlPanel = createControlPanel();
            add(controlPanel, BorderLayout.NORTH);

            // Animation panel with custom sizing
            animationPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    GanttChartPanel.this.paintGanttChart((Graphics2D) g);
                }

                @Override
                public Dimension getPreferredSize() {
                    if (gantt == null || gantt.isEmpty()) {
                        return new Dimension(600, 60);
                    }
                    // Calculate width needed: (20 for left margin + 18 per time unit + right
                    // padding)
                    int width = 20 + (gantt.size() * 18) + 20;
                    return new Dimension(Math.max(width, 600), 60);
                }
            };
            animationPanel.setBackground(Mainframe.BG_DARK);
            add(animationPanel, BorderLayout.CENTER);

            // Timer for animation
            animationTimer = new Timer(500, e -> {
                if (gantt != null && currentTime < gantt.size()) {
                    currentTime++;
                    repaint();
                } else if (gantt != null && currentTime >= gantt.size()) {
                    stopAnimation();
                }
            });
        }

        private JPanel createControlPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
            panel.setBackground(Mainframe.BG_LIGHT_GRAY);
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            timerLabel = new JLabel("Time: 0");
            timerLabel.setFont(new Font("Arial", Font.BOLD, 10));
            timerLabel.setForeground(Color.WHITE);

            playPauseBtn = new JButton("▶");
            playPauseBtn.setBackground(new Color(50, 150, 50));
            playPauseBtn.setForeground(Color.WHITE);
            playPauseBtn.setFocusPainted(false);
            playPauseBtn.setPreferredSize(new Dimension(25, 20));
            playPauseBtn.setFont(new Font("Arial", Font.PLAIN, 8));
            playPauseBtn.addActionListener(e -> toggleAnimation());

            resetBtn = new JButton("⟲");
            resetBtn.setBackground(new Color(150, 100, 50));
            resetBtn.setForeground(Color.WHITE);
            resetBtn.setFocusPainted(false);
            resetBtn.setPreferredSize(new Dimension(25, 20));
            resetBtn.setFont(new Font("Arial", Font.PLAIN, 8));
            resetBtn.addActionListener(e -> resetAnimation());

            panel.add(timerLabel);
            panel.add(playPauseBtn);
            panel.add(resetBtn);

            return panel;
        }

        private void toggleAnimation() {
            if (gantt == null || gantt.isEmpty())
                return;

            if (isRunning) {
                stopAnimation();
            } else {
                startAnimation();
            }
        }

        private void startAnimation() {
            isRunning = true;
            playPauseBtn.setText("⏸ Pause");
            playPauseBtn.setBackground(new Color(150, 50, 50));
            animationTimer.start();
        }

        private void stopAnimation() {
            isRunning = false;
            playPauseBtn.setText("▶ Play");
            playPauseBtn.setBackground(new Color(50, 150, 50));
            animationTimer.stop();
        }

        private void resetAnimation() {
            stopAnimation();
            currentTime = 0;
            timerLabel.setText("Time: 0");
            repaint();
        }

        public void setGanttData(List<Integer> gantt) {
            this.gantt = gantt;
            resetAnimation();
            // Force layout recalculation for dynamic sizing
            animationPanel.revalidate();
            animationPanel.repaint();
            SwingUtilities.invokeLater(() -> {
                Container parent = animationPanel.getParent();
                if (parent != null) {
                    parent.revalidate();
                }
            });
        }

        private void paintGanttChart(Graphics2D g2) {
            if (gantt == null || gantt.isEmpty())
                return;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 4;
            int y = 2;
            int blockHeight = 10;
            int blockWidth = 12;

            // Group consecutive processes
            List<Integer> groupedPids = new ArrayList<>();
            List<Integer> groupedDurations = new ArrayList<>();

            int pid = gantt.get(0);
            int duration = 1;

            for (int i = 1; i < gantt.size(); i++) {
                if (gantt.get(i) == pid) {
                    duration++;
                } else {
                    groupedPids.add(pid);
                    groupedDurations.add(duration);
                    pid = gantt.get(i);
                    duration = 1;
                }
            }
            groupedPids.add(pid);
            groupedDurations.add(duration);

            // Draw all blocks
            int currentX = x;
            int cumulativeTime = 0;

            for (int i = 0; i < groupedPids.size(); i++) {
                int processPid = groupedPids.get(i);
                int dur = groupedDurations.get(i);
                int blockWidthForDuration = blockWidth * dur;

                Color color = Color.getHSBColor((float) processPid / 10, 0.6f, 0.8f);

                // Highlight current process
                if (currentTime >= cumulativeTime && currentTime < cumulativeTime + dur) {
                    g2.setColor(new Color(255, 255, 0, 100)); // Yellow highlight
                    g2.fillRect(currentX - 3, y - 3, blockWidthForDuration + 6, blockHeight + 6);
                    g2.setStroke(new BasicStroke(3));
                    g2.setColor(Color.YELLOW);
                    g2.drawRect(currentX - 3, y - 3, blockWidthForDuration + 6, blockHeight + 6);
                }

                // Draw process block
                g2.setColor(color);
                g2.fillRect(currentX, y, blockWidthForDuration, blockHeight);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(currentX, y, blockWidthForDuration, blockHeight);

                // Draw process ID (small font)
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 4));
                FontMetrics fm = g2.getFontMetrics();
                String pidStr = "P" + processPid;
                int textX = currentX + Math.max(0, (blockWidthForDuration - fm.stringWidth(pidStr)) / 2);
                int textY = y + ((blockHeight - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(pidStr, textX, textY);

                cumulativeTime += dur;
                currentX += blockWidthForDuration;
            }

            // Draw simple footer
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(new Font("Arial", Font.PLAIN, 5));
            g2.drawString("T:" + gantt.size(), x, y + blockHeight + 5);

            // Update timer label
            timerLabel.setText("Time: " + currentTime + " / " + gantt.size());
        }
    }
}