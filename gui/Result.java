package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Result extends JPanel {

    private Mainframe mainframe;

    public Result(Mainframe frame) {
        this.mainframe = frame;
        setLayout(new BorderLayout());
        setBackground(Mainframe.BG_DARK);

        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Mainframe.BG_LIGHT_GRAY);
        JLabel mainPageLabel = new JLabel(" Simulation Output");
        headerPanel.add(mainPageLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Wrapper for the main content to create the white border effect
        JPanel whiteWrapper = new JPanel(new BorderLayout());
        whiteWrapper.setBackground(Color.WHITE);
        whiteWrapper.setBorder(new EmptyBorder(5, 5, 5, 5));

        // === Center Content (Gray Area with Table) ===
        JPanel contentBody = new JPanel(new BorderLayout());
        contentBody.setBackground(Mainframe.BG_LIGHT_GRAY);
        contentBody.setBorder(new EmptyBorder(20, 20, 0, 20));

        // Content Header (Algo Name and Back Button)
        JPanel contentHeader = new JPanel(new BorderLayout());
        contentHeader.setBackground(Mainframe.BG_LIGHT_GRAY);
        JLabel algoNameLbl = new JLabel("Algorithm Name");
        algoNameLbl.setFont(new Font("Arial", Font.BOLD, 14));

        JButton backButton = new JButton("<-"); // Placeholder for back arrow icon
        backButton.setBackground(Mainframe.BG_DARK);
        backButton.setForeground(Mainframe.TEXT_LIGHT);
        backButton.setFocusPainted(false);
        // Action to go back to Schedule screen
        backButton.addActionListener(e -> mainframe.showCard("SCHEDULE"));

        contentHeader.add(algoNameLbl, BorderLayout.WEST);
        contentHeader.add(backButton, BorderLayout.EAST);
        contentBody.add(contentHeader, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Process ID", "Burst time", "Arrival time", "Priority Number", "Waiting Time", "Turnaround Time", "Average Waiting Time", "Average Turnaround Time"};
        // Dummy data based on the image
        Object[][] data = {
                {"P0", "3", "0", "1", "0", "3", "0", "2"},
                {"P1", "6", "1", "2", "1", "5", "1", "4"},
                {"P2", "9", "2", "3", "2", "8", "2", "8"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(new Color(200, 215, 200)); // Greenish header
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setBackground(Mainframe.BG_LIGHT_GRAY);
        // Crude way to make the last two columns look like headers spanning rows
        // In a real app, you might use a distinct panel for averages below the table.
        table.getColumnModel().getColumn(6).setCellRenderer(new GrayHeaderRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new GrayHeaderRenderer());


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Mainframe.BG_LIGHT_GRAY);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        // Add spacing around table
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(Mainframe.BG_LIGHT_GRAY);
        tableWrapper.setBorder(new EmptyBorder(20, 0, 20, 0));
        tableWrapper.add(scrollPane);

        contentBody.add(tableWrapper, BorderLayout.CENTER);


        // === Bottom Gantt Chart Section ===
        GanttChartPanel ganttPanel = new GanttChartPanel();
        // Add the content body and the gantt panel to the white wrapper
        whiteWrapper.add(contentBody, BorderLayout.CENTER);
        whiteWrapper.add(ganttPanel, BorderLayout.SOUTH);

        add(whiteWrapper, BorderLayout.CENTER);
    }

    // Custom renderer to make the average columns look different
    private static class GrayHeaderRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(new Color(200, 215, 200));
            c.setFont(new Font("Arial", Font.BOLD, 12));
            // Only show value in the last row for effect based on image
            if(row < table.getRowCount()-1) setText("");
            return c;
        }
    }

    // Custom Panel for drawing the Gantt Chart based on Image 4
    private class GanttChartPanel extends JPanel {
        public GanttChartPanel() {
            setBackground(Mainframe.BG_DARK);
            setPreferredSize(new Dimension(getHeight(), 200));
            setLayout(null); // Absolute positioning for labels

            JLabel header = new JLabel("Gantt Chart");
            header.setForeground(Mainframe.TEXT_LIGHT);
            header.setBounds(20, 10, 100, 20);
            add(header);

            JLabel timer = new JLabel("Timer: 00:09");
            timer.setForeground(Mainframe.TEXT_LIGHT);
            timer.setBounds(20, 170, 100, 20);
            add(timer);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int startX = 20;
            int startY = 50;
            int height = 40;
            // Hardcoded scale for demonstration based on image data (3, 6, 9 total 18 units)
            int scaleFactor = 20;

            // P1 (Brown) - Duration 3
            int p1Width = 3 * scaleFactor;
            g2d.setColor(new Color(102, 51, 0)); // Brown
            g2d.fillRect(startX, startY, p1Width, height);
            g2d.setColor(Color.WHITE);
            g2d.drawString("P1", startX + p1Width / 2 - 5, startY + height / 2 + 5);
            g2d.drawString("3", startX, startY + height + 20);

            // P2 (Teal) - Duration 3 (ends at 6)
            int p2Width = 3 * scaleFactor;
            g2d.setColor(new Color(0, 102, 102)); // Teal
            g2d.fillRect(startX + p1Width, startY, p2Width, height);
            g2d.setColor(Color.WHITE);
            g2d.drawString("P2", startX + p1Width + p2Width / 2 - 5, startY + height / 2 + 5);
            g2d.drawString("6", startX + p1Width, startY + height + 20);

            // P3 (Green) - Duration 3 (ends at 9)
            int p3Width = 3 * scaleFactor;
            g2d.setColor(new Color(51, 153, 0)); // Green
            g2d.fillRect(startX + p1Width + p2Width, startY, p3Width, height);
            g2d.setColor(Color.WHITE);
            g2d.drawString("P3", startX + p1Width + p2Width + p3Width / 2 - 5, startY + height / 2 + 5);
            g2d.drawString("9", startX + p1Width + p2Width, startY + height + 20);
        }
    }
}