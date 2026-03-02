package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Menu extends JPanel {

    private Mainframe mainframe;

    // Arrays to hold the dynamic content for each algorithm
    private final String[] algorithms = {
            "1. First Come First Serve",
            "2. Round Robin",
            "3. Shortest Job First (Preemptive)",
            "4. Shortest Job First (Non-preemptive)",
            "5. Priority (Preemptive)",
            "6. Priority (Non-preemptive)"
    };

    private final String[] descriptions = {
            "Executes queued processes in the exact order they arrive in the ready queue. It is simple but can cause long waiting times (convoy effect).",
            "Assigns a fixed time slot (quantum) to each process in a cyclic way. Ideal for time-sharing systems to ensure responsiveness.",
            "Also known as Shortest Remaining Time First (SRTF). The currently running process is preempted if a new process arrives with a shorter remaining burst time.",
            "The process with the shortest expected processing time is selected for execution next. Once started, it runs to completion without interruption.",
            "Processes are scheduled based on priority numbers. A running process will be interrupted and put back in the queue if a higher-priority process arrives.",
            "Processes are scheduled based on priority. The running process completes its entire burst time even if a higher-priority process arrives in the meantime."
    };

    private final String[] imagePlaceholders = {
            "<html><center>Image<br>(Visual representation of<br>First Come First Serve)</center></html>",
            "<html><center>Image<br>(Visual representation of<br>Round Robin)</center></html>",
            "<html><center>Image<br>(Visual representation of<br>SJF Preemptive)</center></html>",
            "<html><center>Image<br>(Visual representation of<br>SJF Non-preemptive)</center></html>",
            "<html><center>Image<br>(Visual representation of<br>Priority Preemptive)</center></html>",
            "<html><center>Image<br>(Visual representation of<br>Priority Non-preemptive)</center></html>"
    };

    public Menu(Mainframe frame) {
        this.mainframe = frame;

        // Use GridBagLayout for proportional sizing
        setLayout(new GridBagLayout());
        setBackground(Mainframe.BG_DARK);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weighty = 1.0;

        // === Left Panel ===
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Mainframe.BG_LIGHT_GRAY);
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("AISA ka!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("(AI for Scheduling Algorithm)");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton startButton = new JButton("START");
        startButton.setFont(new Font("Arial", Font.PLAIN, 28));
        startButton.setBackground(Mainframe.BG_DARK_GRAY_HEADER);
        startButton.setForeground(Mainframe.TEXT_LIGHT);
        startButton.setFocusPainted(false);
        startButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        startButton.addActionListener(e -> mainframe.showCard("SCHEDULE"));

        leftPanel.add(titleLabel);
        leftPanel.add(subtitleLabel);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(startButton);
        leftPanel.add(Box.createVerticalStrut(150));

        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 0.38;
        gbcMain.insets = new Insets(0, 0, 0, 10);
        add(leftPanel, gbcMain);

        // === Right Panel Container ===
        JPanel rightContainer = new JPanel(new GridBagLayout());
        rightContainer.setBackground(Mainframe.BG_DARK);

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.fill = GridBagConstraints.BOTH;
        gbcRight.weightx = 1.0;

        // --- Right Top Container ---
        JPanel rightTop = new JPanel(new GridLayout(1, 2, 15, 0));
        rightTop.setBackground(Mainframe.BG_DARK);

        // Algorithms List Sub-panel
        JPanel algoPanel = new JPanel(new BorderLayout());
        algoPanel.setBackground(Mainframe.BG_LIGHT_GRAY);
        algoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel algoTitle = new JLabel("CPU Scheduling Algorithms:");
        algoTitle.setFont(new Font("Arial", Font.BOLD, 14));
        algoTitle.setBorder(new EmptyBorder(0, 0, 10, 0)); // Space below title
        algoPanel.add(algoTitle, BorderLayout.NORTH);

        JList<String> algoList = new JList<>(algorithms);
        algoList.setFont(new Font("Arial", Font.BOLD, 13));
        algoList.setBackground(Mainframe.BG_LIGHT_GRAY);
        algoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Explicitly tell the list to size itself for exactly 6 items
        algoList.setVisibleRowCount(6); 
        algoList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, false);
                setBorder(new EmptyBorder(3, 5, 3, 5));
                return this;
            }
        });
        
        JScrollPane algoScroll = new JScrollPane(algoList);
        algoScroll.setBorder(null);
        algoScroll.setBackground(Mainframe.BG_LIGHT_GRAY);
        algoPanel.add(algoScroll, BorderLayout.CENTER);

        // Description Sub-panel
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(Mainframe.BG_LIGHT_GRAY);
        descPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JTextArea descriptionArea = new JTextArea(
                "What does it do?:\n\nSelect an algorithm from the list to see its description."
        );
        descriptionArea.setFont(new Font("Arial", Font.BOLD, 15));
        descriptionArea.setBackground(Mainframe.BG_LIGHT_GRAY);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        // Set rows to roughly match the height of the list panel
        descriptionArea.setRows(6); 
        
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(null);
        descScroll.setBackground(Mainframe.BG_LIGHT_GRAY);
        descPanel.add(descScroll, BorderLayout.CENTER);

        rightTop.add(algoPanel);
        rightTop.add(descPanel);

        // WEIGHTY = 0.0 -> Tells layout to make this panel EXACTLY as tall as its content needs, no taller.
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.weighty = 0.0; 
        gbcRight.insets = new Insets(0, 0, 10, 0);
        rightContainer.add(rightTop, gbcRight);

        // --- Right Bottom Panel (Image Placeholder) ---
        JPanel rightBottom = new JPanel(new BorderLayout());
        rightBottom.setBackground(Mainframe.BG_LIGHT_GRAY);
        JLabel imageLabel = new JLabel("<html><center>Image<br>(For visual representation of the<br>user's chosen algorithm)</center></html>", SwingConstants.CENTER);
        imageLabel.setFont(new Font("Arial", Font.BOLD, 36));
        rightBottom.add(imageLabel, BorderLayout.CENTER);

        // WEIGHTY = 1.0 -> Tells layout to give all remaining vertical space to this image panel.
        gbcRight.gridx = 0;
        gbcRight.gridy = 1;
        gbcRight.weighty = 1.0; 
        gbcRight.insets = new Insets(5, 0, 0, 0);
        rightContainer.add(rightBottom, gbcRight);

        // === Add Interaction Logic ===
        algoList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { 
                int selectedIndex = algoList.getSelectedIndex();
                if (selectedIndex != -1) {
                    descriptionArea.setText("What does it do?:\n\n" + descriptions[selectedIndex]);
                    imageLabel.setText(imagePlaceholders[selectedIndex]);
                    descriptionArea.setCaretPosition(0);
                }
            }
        });

        // Add Right Container to Main Layout
        gbcMain.gridx = 1;
        gbcMain.gridy = 0;
        gbcMain.weightx = 0.62;
        gbcMain.insets = new Insets(0, 5, 0, 0);
        add(rightContainer, gbcMain);
    }
}