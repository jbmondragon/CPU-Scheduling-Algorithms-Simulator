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

    private JPanel createHeader() {

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Mainframe.BG_LIGHT_GRAY);

        JLabel mainPageLabel = new JLabel(" Simulation Output");
        headerPanel.add(mainPageLabel, BorderLayout.WEST);

        return headerPanel;
    }

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

    private JPanel createContentBody() {

        JPanel contentBody = new JPanel(new BorderLayout());
        contentBody.setBackground(Mainframe.BG_LIGHT_GRAY);
        contentBody.setBorder(new EmptyBorder(20, 20, 0, 20));

        contentBody.add(createContentHeader(), BorderLayout.NORTH);
        contentBody.add(createTableSection(), BorderLayout.CENTER);
        contentBody.add(createStatsPanel(), BorderLayout.SOUTH);

        return contentBody;
    }

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
        table.getTableHeader().setReorderingAllowed(false);
        table.setBackground(Mainframe.BG_LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(Mainframe.BG_LIGHT_GRAY);
        tableWrapper.setBorder(new EmptyBorder(20, 0, 20, 0));
        tableWrapper.add(scrollPane);

        return tableWrapper;
    }

    /**
     * Update the display for a completed simulation.
     *
     * @param algorithmName name of the chosen algorithm
     * @param result        simulation results (jobs + stats)
     * @param quantumTime   quantum value used (only meaningful for Round Robin)
     */
    public void displayResult(String algorithmName, ScheduleResult result, int quantumTime) {

        algoNameLbl.setText(algorithmName);
        model.setRowCount(0);

        for (Job job : result.jobs) {
            model.addRow(new Object[] {
                    job.processID,
                    job.burstTime,
                    job.arrivalTime,
                    job.priorityNumber,
                    job.waitingTime,
                    job.turnaroundTime
            });
        }

        ganttPanel.setGanttData(result);

        // Update stats
        avgWaitingTimeLbl.setText(String.format("Average Waiting Time: %.2f", result.averageWaitingTime));
        avgTurnaroundTimeLbl.setText(String.format("Average Turnaround Time: %.2f", result.averageTurnaroundTime));

        boolean isRR = "Round Robin".equals(algorithmName);
        quantumTimeLbl.setText(isRR ? "Quantum Time: " + quantumTime : "");
        quantumTimeLbl.setVisible(isRR);
    }

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
        quantumTimeLbl.setVisible(false);

        statsPanel.add(avgWaitingTimeLbl);
        statsPanel.add(avgTurnaroundTimeLbl);
        statsPanel.add(quantumTimeLbl);

        return statsPanel;
    }

    /**
     * Gantt Chart Panel for CPU Scheduling visualization with animation controls.
     * Displays the complete execution schedule with:
     * - Colored blocks for each process or idle period
     * - Process IDs inside blocks
     * - Waiting times below blocks
     * - Timeline numbers and animation simulation
     */
    private class GanttChartPanel extends JPanel {

        private List<Integer> gantt;
        private ScheduleResult scheduleResult;
        private int currentTime = 0;
        private final Timer animationTimer;
        private boolean isRunning = false;
        private JButton playPauseBtn;
        private JButton resetBtn;
        private JLabel timerLabel;
        private final JPanel chartPanel;

        public GanttChartPanel() {
            setLayout(new BorderLayout());
            setBackground(Mainframe.BG_DARK);
            setPreferredSize(new Dimension(0, 180));

            JPanel controlPanel = createControlPanel();
            add(controlPanel, BorderLayout.NORTH);

            chartPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (gantt != null && !gantt.isEmpty()) {
                        paintAnimatedGanttChart((Graphics2D) g);
                    }
                }
            };
            chartPanel.setBackground(Mainframe.BG_DARK);
            add(chartPanel, BorderLayout.CENTER);

            animationTimer = new Timer(400, e -> {
                if (gantt != null && currentTime < gantt.size()) {
                    currentTime++;
                    timerLabel.setText("Time: " + currentTime + " / " + gantt.size());
                    chartPanel.repaint();
                } else if (gantt != null && currentTime >= gantt.size()) {
                    stopAnimation();
                }
            });
        }

        private JPanel createControlPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            panel.setBackground(Mainframe.BG_LIGHT_GRAY);
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            timerLabel = new JLabel("Time: 0");
            timerLabel.setFont(new Font("Arial", Font.BOLD, 12));
            timerLabel.setForeground(Color.black);

            playPauseBtn = new JButton("▶ Play");
            playPauseBtn.setBackground(new Color(50, 150, 50));
            playPauseBtn.setForeground(Color.WHITE);
            playPauseBtn.setFocusPainted(false);
            playPauseBtn.setPreferredSize(new Dimension(80, 25));
            playPauseBtn.setFont(new Font("Arial", Font.PLAIN, 10));
            playPauseBtn.addActionListener(e -> toggleAnimation());

            resetBtn = new JButton("⟲ Reset");
            resetBtn.setBackground(new Color(150, 100, 50));
            resetBtn.setForeground(Color.WHITE);
            resetBtn.setFocusPainted(false);
            resetBtn.setPreferredSize(new Dimension(80, 25));
            resetBtn.setFont(new Font("Arial", Font.PLAIN, 10));
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
            if (currentTime >= gantt.size()) {
                currentTime = 0;
            }
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
            chartPanel.repaint();
        }

        public void setGanttData(ScheduleResult result) {
            this.gantt = result.ganttChart;
            this.scheduleResult = result;
            this.currentTime = 0;
            timerLabel.setText("Time: 0");
            chartPanel.revalidate();
            chartPanel.repaint();
        }

        /**
         * Paint the Gantt chart with animation.
         * Shows the chart progressively based on currentTime.
         */
        private void paintAnimatedGanttChart(Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 20;
            int y = 10;
            int blockHeight = 40;
            int timelineY = y + blockHeight + 25;
            int statusY = timelineY + 30;

            // ============================================================
            // STEP 1: Group consecutive processes into single blocks
            // ============================================================
            List<Integer> groupedPids = new ArrayList<>();
            List<Integer> groupedDurations = new ArrayList<>();

            int currentPid = gantt.get(0);
            int duration = 1;

            for (int i = 1; i < gantt.size(); i++) {
                if (gantt.get(i).equals(currentPid)) {
                    duration++;
                } else {
                    groupedPids.add(currentPid);
                    groupedDurations.add(duration);
                    currentPid = gantt.get(i);
                    duration = 1;
                }
            }
            groupedPids.add(currentPid);
            groupedDurations.add(duration);

            // ============================================================
            // STEP 2: Draw grouped blocks progressively based on animation time
            // ============================================================
            int currentX = x;
            int visibleTimeUnits = 0;
            int maxTime = 0;

            for (int i = 0; i < groupedPids.size(); i++) {
                int pid = groupedPids.get(i);
                int dur = groupedDurations.get(i);
                int blockWidth = 65;

                if (visibleTimeUnits < currentTime) {
                    Color blockColor = (pid == -1) ? new Color(200, 200, 200)
                            : Color.getHSBColor((float) pid / 10, 0.7f, 0.8f);

                    g2.setColor(blockColor);
                    g2.fillRect(currentX, y, blockWidth, blockHeight);

                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRect(currentX, y, blockWidth, blockHeight);

                    // Draw Process ID and Duration
                    String pidLabel = (pid == -1) ? "Idle" : "P" + pid;
                    g2.setColor(pid == -1 ? Color.BLACK : Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    int pidX = currentX + (blockWidth - fm.stringWidth(pidLabel)) / 2;
                    int pidY = y + (blockHeight / 2) - 8;
                    g2.drawString(pidLabel, pidX, pidY);

                    // Draw duration below process ID
                    g2.setFont(new Font("Arial", Font.PLAIN, 9));
                    String durLabel = "t:" + dur;
                    FontMetrics durFm = g2.getFontMetrics();
                    int durX = currentX + (blockWidth - durFm.stringWidth(durLabel)) / 2;
                    int durY = pidY + 12;
                    g2.drawString(durLabel, durX, durY);

                    // ============================================================
                    // STEP 3: Draw waiting time below blocks
                    // ============================================================
                    if (pid != -1 && scheduleResult != null) {
                        int waitingTime = 0;
                        for (Job job : scheduleResult.jobs) {
                            int jobPid = Integer.parseInt(job.processID.substring(1));
                            if (jobPid == pid) {
                                waitingTime = job.waitingTime;
                                break;
                            }
                        }
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("Arial", Font.PLAIN, 10));
                        String wtLabel = "WT: " + waitingTime;
                        FontMetrics wtFm = g2.getFontMetrics();
                        int wtX = currentX + (blockWidth - wtFm.stringWidth(wtLabel)) / 2;
                        int wtY = y + blockHeight + 12;
                        g2.drawString(wtLabel, wtX, wtY);
                    }
                }

                currentX += blockWidth;
                visibleTimeUnits += dur;
                maxTime += dur;
            }

            // ============================================================
            // STEP 4: Draw timeline numbers
            // ============================================================
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 11));

            int timeX = x;
            int timeCounter = 0;
            for (int i = 0; i < groupedPids.size(); i++) {
                int dur = groupedDurations.get(i);
                int blockWidth = 65; // Fixed width for all blocks

                if (timeCounter <= currentTime) {
                    g2.drawString(String.valueOf(timeCounter), timeX - 5, timelineY);
                }
                timeX += blockWidth;
                timeCounter += dur;
            }
            // Draw final time marker
            if (timeCounter <= currentTime + 1) {
                g2.drawString(String.valueOf(timeCounter), timeX - 5, timelineY);
            }

            // ============================================================
            // STEP 5: Draw status information
            // ============================================================
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.ITALIC, 10));
            g2.drawString("Total Time: " + maxTime + " units", x, statusY);
        }
    }
}
