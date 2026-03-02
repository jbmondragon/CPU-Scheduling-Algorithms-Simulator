package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Schedule extends JPanel {

    private Mainframe mainframe;

    public Schedule(Mainframe frame) {
        this.mainframe = frame;
        setLayout(new BorderLayout());
        setBackground(Mainframe.BG_DARK);

        // === Top Headers ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Mainframe.BG_LIGHT_GRAY);
        JLabel mainPageLabel = new JLabel(" Main Page");
        headerPanel.add(mainPageLabel, BorderLayout.WEST);

        JLabel aisaLabel = new JLabel("AISA ", SwingConstants.RIGHT);
        aisaLabel.setBackground(Mainframe.BG_DARK);
        aisaLabel.setOpaque(true);
        aisaLabel.setForeground(Mainframe.TEXT_LIGHT);
        headerPanel.add(aisaLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);


        // === Main Content Area ===
        JPanel contentBody = new JPanel(new BorderLayout());
        contentBody.setBackground(Mainframe.BG_LIGHT_GRAY);

        JLabel returnLink = new JLabel("<html><u>Return</u></html>");
        returnLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        returnLink.setBorder(new EmptyBorder(10, 20, 10, 0));
        // Action to go back to Menu
        returnLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainframe.showCard("MENU");
            }
        });
        contentBody.add(returnLink, BorderLayout.NORTH);

        // The split section for Algorithm selection and Process handling
        JPanel splitContainer = new JPanel(new GridLayout(1, 2, 30, 0));
        splitContainer.setBackground(Mainframe.BG_LIGHT_GRAY);
        splitContainer.setBorder(new EmptyBorder(20, 30, 50, 30));

        // --- Left: Algorithm Selection ---
        JPanel algoPanel = new JPanel(new BorderLayout());
        JPanel algoHeader = createDarkHeader("Select A Scheduling Algorithm To Simulate:");
        algoPanel.add(algoHeader, BorderLayout.NORTH);

        JPanel algoBody = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 30));
        algoBody.setBackground(Mainframe.BG_MEDIUM_GRAY);

        String[] algorithms = {"First Come First Serve", "Round Robin", "SJF (Preemptive)", "SJF (Non-preemptive)", "Priority (Preemptive)", "Priority (Non-preemptive)"};
        JComboBox<String> algoCombo = new JComboBox<>(algorithms);
        algoCombo.setPreferredSize(new Dimension(200, 30));

        JButton submitBtn = new JButton("Submit");
        submitBtn.setPreferredSize(new Dimension(100, 40));
        submitBtn.setBackground(Color.WHITE);
        // Temporary action to show result page
        submitBtn.addActionListener(e -> mainframe.showCard("RESULT"));

        algoBody.add(algoCombo);
        algoBody.add(submitBtn);
        algoPanel.add(algoBody, BorderLayout.CENTER);


        // --- Right: Processes ---
        JPanel processPanel = new JPanel(new BorderLayout());
        // Header with placeholder icons
        JPanel processHeader = createDarkHeader("Processes");
        JLabel subtitle = new JLabel("Add a process for simulation");
        subtitle.setForeground(Color.GRAY);
        subtitle.setBorder(new EmptyBorder(0, 10, 5, 0));
        processHeader.add(subtitle, BorderLayout.SOUTH);
        // Placeholder for icons on the right of header
        JLabel iconsPlaceholder = new JLabel("[->] [::] "); // Replace with ImageIcons
        iconsPlaceholder.setForeground(Color.WHITE);
        processHeader.add(iconsPlaceholder, BorderLayout.EAST);

        processPanel.add(processHeader, BorderLayout.NORTH);

        JPanel processBody = new JPanel(new GridBagLayout());
        processBody.setBackground(Mainframe.BG_LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JButton addProcBtn = createRoundedButton("Add Process", Color.WHITE, Color.BLACK);
        processBody.add(addProcBtn, gbc);

        gbc.gridy = 1;
        JButton clearAllBtn = createRoundedButton("Clear All", Mainframe.BG_MEDIUM_GRAY, Color.BLACK);
        processBody.add(clearAllBtn, gbc);

        processPanel.add(processBody, BorderLayout.CENTER);

        splitContainer.add(algoPanel);
        splitContainer.add(processPanel);
        contentBody.add(splitContainer, BorderLayout.CENTER);

        // Wrapper to give the white border effect inside the black frame
        JPanel whiteWrapper = new JPanel(new BorderLayout());
        whiteWrapper.setBackground(Color.WHITE);
        whiteWrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
        whiteWrapper.add(contentBody);

        add(whiteWrapper, BorderLayout.CENTER);
    }

    // Helper for the dark headers in the split panes
    private JPanel createDarkHeader(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Mainframe.BG_DARK);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Mainframe.TEXT_LIGHT);
        label.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    // Helper for rounded buttons (approximate look)
    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return btn;
    }
}