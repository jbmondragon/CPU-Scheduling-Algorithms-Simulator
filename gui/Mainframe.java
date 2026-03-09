package gui;

import java.awt.*;
import javax.swing.*;

public class Mainframe extends JFrame {

    // Define common application colors based on mockups
    public static final Color BG_DARK = Color.BLACK;
    public static final Color BG_LIGHT_GRAY = new Color(220, 220, 220);
    public static final Color BG_MEDIUM_GRAY = new Color(169, 169, 169);
    public static final Color BG_DARK_GRAY_HEADER = new Color(64, 64, 64);
    public static final Color TEXT_DARK = Color.BLACK;
    public static final Color TEXT_LIGHT = Color.WHITE;

    public CardLayout cardLayout;
    public JPanel mainPanel;
    private final Result resultPanel;

    public Mainframe() {
        setTitle("CPU Scheduling Simulator (AISA)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // prevent user from shrinking window too far
        setMinimumSize(new Dimension(1024, 720));
        setResizable(false);
        setLocationRelativeTo(null);

        // Main panel with CardLayout to switch views
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Instantiate the views
        // We pass 'this' (the Mainframe instance) so panels can call methods like
        // showCard()
        Menu menuPanel = new Menu(this);
        Schedule schedulePanel = new Schedule(this);
        resultPanel = new Result(this);

        // Add views to the card layout with unique names
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(schedulePanel, "SCHEDULE");
        mainPanel.add(resultPanel, "RESULT");

        add(mainPanel);

        // Show the initial screen
        cardLayout.show(mainPanel, "MENU");
    }

    // Method used by child panels to switch screens
    public void showCard(String cardName) {
        cardLayout.show(mainPanel, cardName);
    }

    // Method to retrieve the result panel
    public Result getResultPanel() {
        return resultPanel;
    }
}