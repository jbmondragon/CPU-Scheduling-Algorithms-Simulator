
package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Help extends JPanel {

    private final Mainframe mainframe;

    // Color scheme matching the application
    private static final Color HELP_BG = new Color(240, 240, 240);
    private static final Color SECTION_BG = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color TIP_BG = new Color(255, 255, 200);

    public Help(Mainframe frame) {
        this.mainframe = frame;

        setLayout(new BorderLayout());
        setBackground(Mainframe.BG_DARK);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Mainframe.BG_DARK);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Help & How to Play");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Mainframe.TEXT_LIGHT);

        JButton backButton = new JButton("← Back to Menu");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBackground(new Color(100, 100, 100));
        backButton.setForeground(Mainframe.TEXT_LIGHT);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        backButton.addActionListener(e -> mainframe.showCard("MENU"));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(HELP_BG);
        mainContent.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Create tabbed pane for organized help sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(HELP_BG);

        // Add all help sections as tabs
        tabbedPane.addTab("How to Play", createHowToPlayPanel());
        tabbedPane.addTab("Scheduling Algorithms", createAlgorithmsPanel());
        tabbedPane.addTab("Input Guide", createInputGuidePanel());
        tabbedPane.addTab("Tips & Tricks", createTipsPanel());
        tabbedPane.addTab("FAQ", createFAQPanel());

        mainContent.add(tabbedPane, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createHowToPlayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HELP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create scrollable content
        JTextArea content = new JTextArea();
        content.setEditable(false);
        content.setFont(new Font("Arial", Font.PLAIN, 14));
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setBackground(HELP_BG);

        content.setText(
                """
                        WELCOME TO OSA (Optimal Scheduling Algorithm) SIMULATOR

                        This is an interactive CPU scheduling simulator where you can experiment with different scheduling algorithms and see how they perform!

                        STEP-BY-STEP GUIDE:

                        1. START THE SIMULATION
                           • From the main menu, click the "START" button
                           • You'll be taken to the scheduling configuration screen

                        2. CREATE PROCESSES
                           • You'll see a table with columns for Process ID, Burst Time, Arrival Time, and Priority Number
                           • Minimum 3 processes required, maximum 20 allowed
                           • Fill in the data for each process:
                             - Burst Time: How long the process needs CPU (1-30)
                             - Arrival Time: When the process arrives (0-30)
                             - Priority Number: Only used for Priority algorithms (1-20, unique)
                           • Process IDs are automatically assigned (P1, P2, etc.)

                        3. USE THE ACTION BUTTONS (right side)
                           • Add Process: Add a new empty row
                           • Clear All: Remove all processes and start fresh
                           • Random: Generate random process data for testing
                           • Import: Load processes from a file (.txt, .csv, .xlsx)

                        4. SELECT AN ALGORITHM
                           • Choose from 6 different scheduling algorithms
                           • For Round Robin, you'll need to set a Quantum Time (1-10)
                           • For Priority algorithms, toggle whether higher numbers = higher priority

                        5. RUN SIMULATION
                           • Click the "Submit" button at the bottom
                           • Watch the simulation run in real-time!

                        6. ANALYZE RESULTS
                           • View the Gantt chart showing process execution
                           • Check waiting times and turnaround times in the table
                           • Use Play/Pause/Replay controls to review the execution
                           • Click "←" to go back and try different settings

                        That's it! Experiment with different process sets and algorithms to see how they affect performance!
                        """);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAlgorithmsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(HELP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[][] algorithms = {
                { "First Come First Serve (FCFS)",
                        "• Simplest algorithm - processes run in order of arrival\n" +
                                "• Non-preemptive - once started, a process runs to completion\n" +
                                "• Can cause convoy effect where short jobs wait for long jobs\n" +
                                "• Best for: Batch systems where order matters" },

                { "Round Robin (RR)",
                        "• Each process gets a fixed time slice (quantum)\n" +
                                "• Preemptive - processes are cycled through the CPU\n" +
                                "• Quantum size affects performance: too small = more context switching, too large = behaves like FCFS\n"
                                +
                                "• Best for: Time-sharing systems, interactive environments" },

                { "SJF (Preemptive) - SRTF",
                        "• Shortest Remaining Time First - always runs the process with least remaining time\n" +
                                "• Preemptive - new shorter job can interrupt current one\n" +
                                "• Provides minimum average waiting time theoretically\n" +
                                "• Best for: Systems where minimizing waiting time is critical" },

                { "SJF (Non-preemptive)",
                        "• Selects shortest job when CPU becomes available\n" +
                                "• Non-preemptive - runs selected job to completion\n" +
                                "• Simpler than preemptive version but may not be optimal\n" +
                                "• Best for: Batch systems with known job durations" },

                { "Priority (Preemptive)",
                        "• Higher/lower priority number determines execution order\n" +
                                "• Preemptive - higher priority job can interrupt lower priority one\n" +
                                "• Can cause starvation of low priority processes\n" +
                                "• Best for: Systems with clear priority levels (OS tasks, user apps)" },

                { "Priority (Non-preemptive)",
                        "• Priority-based selection when CPU becomes available\n" +
                                "• Non-preemptive - once started, runs to completion\n" +
                                "• Less overhead than preemptive version\n" +
                                "• Best for: Systems where priorities don't change frequently" }
        };

        for (String[] algo : algorithms) {
            panel.add(createAlgorithmCard(algo[0], algo[1]));
            panel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(HELP_BG);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createAlgorithmCard(String title, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(SECTION_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(ACCENT_COLOR);

        JTextArea descArea = new JTextArea(description);
        descArea.setEditable(false);
        descArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descArea.setBackground(SECTION_BG);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(new EmptyBorder(10, 0, 0, 0));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descArea, BorderLayout.CENTER);

        return card;
    }

    private JPanel createInputGuidePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HELP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(HELP_BG);

        // Input Fields Guide
        contentPanel.add(createSectionTitle("Input Fields Guide"));
        contentPanel.add(Box.createVerticalStrut(15));

        String[][] fields = {
                { "Process ID", "Auto-generated as P1, P2, etc. Must be unique if manually entered." },
                { "Burst Time", "Range: 1-30. The total CPU time needed by the process." },
                { "Arrival Time", "Range: 0-30. When the process enters the ready queue." },
                { "Priority Number", "Range: 1-20. Must be unique. Lower/Higher number priority depends on setting." }
        };

        for (String[] field : fields) {
            contentPanel.add(createFieldGuideRow(field[0], field[1]));
            contentPanel.add(Box.createVerticalStrut(10));
        }

        contentPanel.add(Box.createVerticalStrut(20));

        // File Import Guide
        contentPanel.add(createSectionTitle("File Import Format"));
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel fileFormatPanel = new JPanel(new BorderLayout());
        fileFormatPanel.setBackground(SECTION_BG);
        fileFormatPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextArea fileFormat = new JTextArea();
        fileFormat.setEditable(false);
        fileFormat.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fileFormat.setBackground(SECTION_BG);
        fileFormat.setText("""
                TXT/CSV Format (space or comma separated):
                ----------------------------------------
                P1 5 0 1
                P2 8 2 2
                P3 3 4 3

                Or with headers:
                Process,Burst,Arrival,Priority
                P1,5,0,1
                P2,8,2,2
                P3,3,4,3

                XLSX Format:
                - First row can be headers or data
                - Columns: Process ID, Burst Time, Arrival Time, Priority
                """);

        fileFormatPanel.add(fileFormat, BorderLayout.CENTER);
        contentPanel.add(fileFormatPanel);

        contentPanel.add(Box.createVerticalStrut(20));

        // Example
        contentPanel.add(createSectionTitle("Example Input"));
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel examplePanel = new JPanel(new BorderLayout());
        examplePanel.setBackground(SECTION_BG);
        examplePanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JTextArea example = new JTextArea();
        example.setEditable(false);
        example.setFont(new Font("Monospaced", Font.PLAIN, 12));
        example.setBackground(SECTION_BG);
        example.setText("""
                Process    Burst    Arrival    Priority
                ----------------------------------------
                P1         10       0          3
                P2         5        2          1
                P3         8        4          2
                P4         3        6          4
                """);

        examplePanel.add(example, BorderLayout.CENTER);
        contentPanel.add(examplePanel);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTipsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HELP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(HELP_BG);

        // Beginner Tips
        contentPanel.add(createSectionTitle("Beginner Tips"));
        contentPanel.add(Box.createVerticalStrut(15));

        String[] beginnerTips = {
                "Start with FCFS - it's the simplest to understand",
                "Try the Random button to generate test data quickly",
                "Watch how the Gantt chart animates to understand process execution",
                "Compare waiting times between different algorithms",
                "Use small burst times (1-5) initially for clearer visualization"
        };

        for (String tip : beginnerTips) {
            contentPanel.add(createTipBox(tip, "Tip:"));
            contentPanel.add(Box.createVerticalStrut(10));
        }

        contentPanel.add(Box.createVerticalStrut(20));

        // Advanced Tips
        contentPanel.add(createSectionTitle("Advanced Tips"));
        contentPanel.add(Box.createVerticalStrut(15));

        String[] advancedTips = {
                "Experiment with Round Robin quantum sizes - see how 2 vs 4 affects performance",
                "Try priority inversion scenarios with Priority algorithms",
                "Test SJF Preemptive with varying arrival times to see preemption in action",
                "Compare SJF vs Priority when priorities match burst times",
                "Create processes that arrive at the same time to see pure scheduling behavior",
                "Use the replay button to review execution multiple times"
        };

        for (String tip : advancedTips) {
            contentPanel.add(createTipBox(tip, "Note:"));
            contentPanel.add(Box.createVerticalStrut(10));
        }

        contentPanel.add(Box.createVerticalStrut(20));

        // Common Patterns
        contentPanel.add(createSectionTitle("Common Patterns to Try"));
        contentPanel.add(Box.createVerticalStrut(15));

        String[][] patterns = {
                { "Convoy Effect", "Long job first followed by short jobs - see FCFS vs SJF" },
                { "Starvation", "Continuous stream of high priority jobs - watch low priority jobs never run" },
                { "Context Switching", "Many short processes with small quantum - observe overhead" },
                { "Priority Inversion", "Medium priority job blocking high priority job indirectly" }
        };

        for (String[] pattern : patterns) {
            JPanel patternCard = new JPanel(new BorderLayout());
            patternCard.setBackground(SECTION_BG);
            patternCard.setBorder(new EmptyBorder(10, 15, 10, 15));

            JLabel title = new JLabel(pattern[0]);
            title.setFont(new Font("Arial", Font.BOLD, 14));
            title.setForeground(ACCENT_COLOR);

            JLabel desc = new JLabel(pattern[1]);
            desc.setFont(new Font("Arial", Font.PLAIN, 12));

            patternCard.add(title, BorderLayout.NORTH);
            patternCard.add(desc, BorderLayout.CENTER);

            contentPanel.add(patternCard);
            contentPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFAQPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(HELP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(HELP_BG);

        String[][] faqs = {
                { "What's the minimum number of processes?", "3 processes minimum, maximum 20." },
                { "Why can't I delete some rows?",
                        "You must keep at least 3 rows. Delete buttons are disabled when at minimum." },
                { "What do the colors in Gantt chart mean?",
                        "Each process has a unique color. Gray blocks indicate idle time." },
                { "Why is my Priority algorithm not working as expected?",
                        "Check if you've set higher/lower number priority correctly." },
                { "Can I save my results?",
                        "Currently, results are shown on screen. Take screenshots for record-keeping." },
                { "What's the difference between waiting and turnaround time?",
                        "Waiting time is time spent in ready queue. Turnaround time is total time from arrival to completion." },
                { "Why does my Round Robin simulation show many context switches?",
                        "Small quantum sizes increase context switches. Try increasing the quantum." },
                { "Can I import data from Excel?",
                        "Yes! Import .xlsx files with Process, Burst, Arrival, Priority columns." },
                { "What happens if I enter invalid data?",
                        "The cell will flash red and show an error message explaining the valid range." },
                { "Why isn't my Gantt chart animating?",
                        "Click the Play button to start animation. Use Replay to restart." }
        };

        for (String[] faq : faqs) {
            contentPanel.add(createFAQItem(faq[0], faq[1]));
            contentPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Helper methods for creating styled components
    private JLabel createSectionTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(ACCENT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createFieldGuideRow(String field, String description) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(HELP_BG);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fieldLabel = new JLabel(field + ":");
        fieldLabel.setFont(new Font("Arial", Font.BOLD, 14));
        fieldLabel.setPreferredSize(new Dimension(120, 25));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        row.add(fieldLabel, BorderLayout.WEST);
        row.add(descLabel, BorderLayout.CENTER);

        return row;
    }

    private JPanel createTipBox(String tip, String prefix) {
        JPanel tipPanel = new JPanel(new BorderLayout(10, 0));
        tipPanel.setBackground(TIP_BG);
        tipPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 0)),
                new EmptyBorder(8, 12, 8, 12)));
        tipPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel prefixLabel = new JLabel(prefix);
        prefixLabel.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel tipLabel = new JLabel(tip);
        tipLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        tipPanel.add(prefixLabel, BorderLayout.WEST);
        tipPanel.add(tipLabel, BorderLayout.CENTER);

        return tipPanel;
    }

    private JPanel createFAQItem(String question, String answer) {
        JPanel faqPanel = new JPanel(new BorderLayout(10, 5));
        faqPanel.setBackground(SECTION_BG);
        faqPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        faqPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel questionLabel = new JLabel("Q: " + question);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        questionLabel.setForeground(ACCENT_COLOR);

        JLabel answerLabel = new JLabel("A: " + answer);
        answerLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        faqPanel.add(questionLabel, BorderLayout.NORTH);
        faqPanel.add(answerLabel, BorderLayout.CENTER);

        return faqPanel;
    }
}
