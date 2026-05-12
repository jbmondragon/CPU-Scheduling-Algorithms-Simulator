package gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import src.*;

public class Result extends JPanel {

    private static final int GANTT_VIEWPORT_HEIGHT = 105;
    private static final int GANTT_CHART_HEIGHT = 82;

    private final Mainframe mainframe;
    private JTable table;
    private DefaultTableModel model;
    private JLabel algoNameLbl;
    private GanttChartPanel ganttPanel;
    private JScrollPane ganttScroll;
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
        JPanel controls = ganttPanel.getControlPanel();
        JPanel chartOnly = ganttPanel.getChartPanel();

        // Wrap only the chart area in a scroll pane (controls remain above)
        // create scroll pane for only the chart area
        ganttScroll = new JScrollPane(chartOnly,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        ganttScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        ganttScroll.setPreferredSize(new Dimension(0, GANTT_VIEWPORT_HEIGHT));

        // container for controls + scrolling chart
        JPanel ganttContainer = new JPanel(new BorderLayout());
        ganttContainer.add(controls, BorderLayout.NORTH);
        ganttContainer.add(ganttScroll, BorderLayout.CENTER);

        whiteWrapper.add(contentBody, BorderLayout.NORTH);
        whiteWrapper.add(ganttContainer, BorderLayout.CENTER);

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
                "Turnaround Time",
                "Avg. Waiting Time",
                "Avg. Turn Around Time"
        };

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
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
                    job.turnaroundTime,
                    String.format("%.2f", result.averageWaitingTime),
                    String.format("%.2f", result.averageTurnaroundTime)
            });
        }

        ganttPanel.setGanttData(result);

        boolean isRR = "Round Robin".equals(algorithmName);
        quantumTimeLbl.setText(isRR ? "Quantum Time: " + quantumTime : "");
        quantumTimeLbl.setVisible(isRR);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        statsPanel.setBackground(Mainframe.BG_LIGHT_GRAY);
        statsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        quantumTimeLbl = new JLabel("Quantum Time: -");
        quantumTimeLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        quantumTimeLbl.setVisible(false);

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

        private static final int TIME_UNIT_WIDTH = 46;
        private static final int CHART_LEFT_PADDING = 20;
        private static final int CHART_TOP_PADDING = 8;
        private static final int BLOCK_HEIGHT = 22;
        private static final int TIMER_DELAY_MS = 40;
        private static final double ANIMATION_STEP = 0.1;

        private List<Integer> gantt;
        private double animationTime = 0;
        private Timer animationTimer;
        private boolean isRunning = false;
        private JButton playPauseBtn;
        private JButton resetBtn;
        private JLabel timerLabel;
        private final JPanel chartPanel;
        private final JPanel controlPanel;

        public GanttChartPanel() {
            setLayout(new BorderLayout());
            setBackground(Mainframe.BG_DARK);
            setPreferredSize(new Dimension(0, GANTT_CHART_HEIGHT));

            controlPanel = createControlPanel();
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

            //Added animation timer to dynamically show the gantt chart movement
            animationTimer = new Timer(TIMER_DELAY_MS, e -> {
                if (gantt != null && animationTime < gantt.size()) {
                    animationTime = Math.min(gantt.size(), animationTime + ANIMATION_STEP);
                    timerLabel.setText("Time: " + String.format("%03d", (int) animationTime));
                    scrollToAnimatedPosition();
                    chartPanel.repaint();
                } else if (gantt != null && animationTime >= gantt.size()) {
                    stopAnimation();
                }
            });
        }

        private JPanel createControlPanel() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            panel.setBackground(Mainframe.BG_LIGHT_GRAY);
            panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            timerLabel = new JLabel("Time: 000");
            timerLabel.setFont(new Font("Arial", Font.BOLD, 12));
            timerLabel.setForeground(Color.black);

            playPauseBtn = new JButton("Play");
            playPauseBtn.setBackground(new Color(50, 150, 50));
            playPauseBtn.setForeground(Color.WHITE);
            playPauseBtn.setFocusPainted(false);
            playPauseBtn.setPreferredSize(new Dimension(80, 25));
            playPauseBtn.setFont(new Font("Arial", Font.PLAIN, 10));
            playPauseBtn.addActionListener(e -> toggleAnimation());

            resetBtn = new JButton("Replay");
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
            if (animationTime >= gantt.size()) {
                animationTime = 0;

                if (ganttScroll != null) {
                    SwingUtilities.invokeLater(() -> ganttScroll.getHorizontalScrollBar().setValue(0));
                }
            }

            isRunning = true;
            playPauseBtn.setText("Pause");
            playPauseBtn.setBackground(new Color(150, 50, 50));
            animationTimer.start();
        }

        private void stopAnimation() {
            isRunning = false;
            playPauseBtn.setText("Play");
            playPauseBtn.setBackground(new Color(50, 150, 50));
            animationTimer.stop();
        }

        // Pag reset sa animation state, timer, and scroll position
        private void resetAnimation() {
            stopAnimation();
            animationTime = 0;
            timerLabel.setText("Time: 000");

            if (ganttScroll != null) {
                SwingUtilities.invokeLater(() -> ganttScroll.getHorizontalScrollBar().setValue(0));
            }

            chartPanel.repaint();
        }

        //pag show han result
        public void setGanttData(ScheduleResult result) {
            this.gantt = result.ganttChart;
            this.animationTime = 0;
            timerLabel.setText("Time: 000");
            // compute width for each time unit
            int width = CHART_LEFT_PADDING * 2;
            if (gantt != null) {
                width += gantt.size() * TIME_UNIT_WIDTH;
            }
            // only chartPanel needs width adjustment now
            chartPanel.setPreferredSize(new Dimension(width, GANTT_CHART_HEIGHT));
            chartPanel.revalidate();
            chartPanel.repaint();
            if (ganttScroll != null) {
                ganttScroll.revalidate();
                ganttScroll.repaint();
            }
        }

        /** Fixed controls panel (not scrolled). */
        public JPanel getControlPanel() {
            return controlPanel;
        }

        /** The actual drawing area inside scroll pane. */
        public JPanel getChartPanel() {
            return chartPanel;
        }

        private void scrollToAnimatedPosition() {
            if (ganttScroll == null) {
                return;
            }

            JScrollBar horizontalBar = ganttScroll.getHorizontalScrollBar();
            int viewportWidth = ganttScroll.getViewport().getWidth();
            int progressX = CHART_LEFT_PADDING + (int) Math.round(animationTime * TIME_UNIT_WIDTH);
            int targetValue = Math.max(0, progressX - (viewportWidth / 2));
            horizontalBar.setValue(targetValue);
        }

        /**
         * Paint the Gantt chart with animation.
         * Shows the chart progressively based on animationTime.
         */
        private void paintAnimatedGanttChart(Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = CHART_LEFT_PADDING;
            int y = CHART_TOP_PADDING;
            int timelineY = y + BLOCK_HEIGHT + 18;

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

            for (int i = 0; i < groupedPids.size(); i++) {
                int pid = groupedPids.get(i);
                int dur = groupedDurations.get(i);
                int blockWidth = dur * TIME_UNIT_WIDTH;
                double filledUnits = Math.max(0, Math.min(dur, animationTime - visibleTimeUnits));
                int filledWidth = (int) Math.round((filledUnits / dur) * blockWidth);

                Color blockColor = (pid == -1) ? new Color(200, 200, 200)
                        : Color.getHSBColor((float) pid / 10, 0.7f, 0.8f);

                if (filledWidth > 0) {
                    g2.setColor(blockColor);
                    g2.fillRect(currentX, y, filledWidth, BLOCK_HEIGHT);

                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRect(currentX, y, filledWidth, BLOCK_HEIGHT);
                }

                if (filledWidth > 18) {
                    String pidLabel = (pid == -1) ? "Idle" : "P" + pid;

                    g2.setColor(pid == -1 ? Color.BLACK : Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 12));

                    FontMetrics fm = g2.getFontMetrics();
                    int pidX = currentX + (filledWidth - fm.stringWidth(pidLabel)) / 2;
                    int pidY = y + ((BLOCK_HEIGHT - fm.getHeight()) / 2) + fm.getAscent();

                    Shape previousClip = g2.getClip();
                    g2.setClip(currentX, y, filledWidth, BLOCK_HEIGHT);
                    g2.drawString(pidLabel, pidX, pidY);
                    g2.setClip(previousClip);
                }

                currentX += blockWidth;
                visibleTimeUnits += dur;
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
                int blockWidth = dur * TIME_UNIT_WIDTH;

                if (timeCounter <= animationTime) {
                    g2.drawString(String.valueOf(timeCounter), timeX - 5, timelineY);
                }
                timeX += blockWidth;
                timeCounter += dur;
            }
            // Draw final time marker
            if (timeCounter <= animationTime) {
                g2.drawString(String.valueOf(timeCounter), timeX - 5, timelineY);
            }

            if (animationTime > 0) {
                int progressX = x + (int) Math.round(animationTime * TIME_UNIT_WIDTH);
                g2.setColor(new Color(255, 255, 255, 180));
                g2.drawLine(progressX, y - 4, progressX, timelineY + 4);
            }
        }
    }
}
