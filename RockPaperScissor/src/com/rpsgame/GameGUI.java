package src.com.rpsgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI implements ActionListener {
    // Swing components
    private JFrame frame;
    private JLabel titleLabel;
    private JLabel playerChoiceLabel;
    private JLabel computerChoiceLabel;
    private JLabel resultLabel;
    private JLabel scoreLabel;
    private JButton rockButton;
    private JButton paperButton;
    private JButton scissorsButton;
    private JButton resetButton;
    private JButton fullScreenButton;
    
    // Game objects
    private Player player;
    private Player computer;
    private GameLogic logic;
    
    // Full screen tracking
    private boolean isFullScreen = false;
    private Rectangle normalBounds;
    
    // Colors for minimal grey theme
    private final Color BG_COLOR = new Color(45, 45, 45);      // Dark grey
    private final Color PANEL_COLOR = new Color(60, 60, 60);   // Medium grey
    private final Color BUTTON_COLOR = new Color(80, 80, 80);   // Light grey
    private final Color TEXT_COLOR = new Color(220, 220, 220);  // Off-white
    
    // Constructor
    public GameGUI() {
        // Create game objects
        player = new Player("You");
        computer = new Player("Computer");
        logic = new GameLogic();
        
        // Set up the GUI
        setupFrame();
        createComponents();
        layoutComponents();
        attachListeners();
        
        // Show the window
        frame.setVisible(true);
    }
    
    // Helper method to get emoji-friendly font
    private Font getEmojiFriendlyFont(int style, int size) {
        // Try common emoji-supporting fonts in order of preference
        String[] fontNames = {"Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji", "Dialog"};
        
        for (String name : fontNames) {
            Font font = new Font(name, style, size);
            // Test if font can display emojis (rock emoji)
            if (font.canDisplay('✊') && font.canDisplay('✋') && font.canDisplay('2')) {
                return font;
            }
        }
        // Fallback to Dialog if no emoji font found
        return new Font("Dialog", style, size);
    }
    
    private void setupFrame() {
        frame = new JFrame("Rock Paper Scissors ✊✋✌️");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setMinimumSize(new Dimension(400, 400));  // Prevents window from getting too small
        frame.setResizable(true);  // Now resizable!
        frame.getContentPane().setBackground(BG_COLOR);
        frame.setLayout(new BorderLayout());
        
        // Add resize listener to make fonts responsive
        frame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                adjustFontSizes(frame.getWidth(), frame.getHeight());
            }
        });
    }
    
    private void adjustFontSizes(int width, int height) {
        // Scale fonts based on window size
        int titleSize = Math.max(16, Math.min(32, width / 20));
        int labelSize = Math.max(12, Math.min(24, width / 25));
        int buttonSize = Math.max(12, Math.min(22, width / 30));
        int scoreSize = Math.max(14, Math.min(26, width / 25));
        
        titleLabel.setFont(getEmojiFriendlyFont(Font.BOLD, titleSize));
        playerChoiceLabel.setFont(getEmojiFriendlyFont(Font.PLAIN, labelSize));
        computerChoiceLabel.setFont(getEmojiFriendlyFont(Font.PLAIN, labelSize));
        resultLabel.setFont(getEmojiFriendlyFont(Font.BOLD, labelSize));
        scoreLabel.setFont(getEmojiFriendlyFont(Font.BOLD, scoreSize));
        
        rockButton.setFont(getEmojiFriendlyFont(Font.BOLD, buttonSize));
        paperButton.setFont(getEmojiFriendlyFont(Font.BOLD, buttonSize));
        scissorsButton.setFont(getEmojiFriendlyFont(Font.BOLD, buttonSize));
        resetButton.setFont(getEmojiFriendlyFont(Font.PLAIN, buttonSize - 2));
        fullScreenButton.setFont(getEmojiFriendlyFont(Font.PLAIN, buttonSize - 2));
    }
    
    private void createComponents() {
        // Title
        titleLabel = new JLabel("Rock Paper Scissors Game", SwingConstants.CENTER);
        titleLabel.setFont(getEmojiFriendlyFont(Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Choice display labels
        playerChoiceLabel = new JLabel("Your choice: ❓", SwingConstants.CENTER);
        playerChoiceLabel.setFont(getEmojiFriendlyFont(Font.PLAIN, 18));
        playerChoiceLabel.setForeground(TEXT_COLOR);
        
        computerChoiceLabel = new JLabel("Computer choice: ❓", SwingConstants.CENTER);
        computerChoiceLabel.setFont(getEmojiFriendlyFont(Font.PLAIN, 18));
        computerChoiceLabel.setForeground(TEXT_COLOR);
        
        // Result label
        resultLabel = new JLabel("Click a button to start! 🎮", SwingConstants.CENTER);
        resultLabel.setFont(getEmojiFriendlyFont(Font.BOLD, 16));
        resultLabel.setForeground(new Color(100, 200, 100)); // Slight green tint for result
        
        // Score label
        scoreLabel = new JLabel("Score: You 0 - 0 Computer", SwingConstants.CENTER);
        scoreLabel.setFont(getEmojiFriendlyFont(Font.BOLD, 20));
        scoreLabel.setForeground(TEXT_COLOR);
        
        // Buttons with emojis
        rockButton = new JButton("✊  ROCK");
        paperButton = new JButton("✋  PAPER");
        scissorsButton = new JButton("✌️  SCISSORS");
        resetButton = new JButton("🔄  RESET SCORE");
        fullScreenButton = new JButton("🖥️  FULL SCREEN");
        
        // Style all buttons
        JButton[] buttons = {rockButton, paperButton, scissorsButton, resetButton, fullScreenButton};
        for (JButton btn : buttons) {
            btn.setBackground(BUTTON_COLOR);
            btn.setForeground(TEXT_COLOR);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor on hover
        }
        
        // Make game buttons larger
        rockButton.setFont(getEmojiFriendlyFont(Font.BOLD, 20));
        paperButton.setFont(getEmojiFriendlyFont(Font.BOLD, 20));
        scissorsButton.setFont(getEmojiFriendlyFont(Font.BOLD, 20));
        resetButton.setFont(getEmojiFriendlyFont(Font.PLAIN, 14));
        fullScreenButton.setFont(getEmojiFriendlyFont(Font.PLAIN, 14));
    }
    
    private void layoutComponents() {
        // Top panel (title)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Center panel (game info)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        centerPanel.add(playerChoiceLabel);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(computerChoiceLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(resultLabel);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(scoreLabel);
        
        // Bottom panel (buttons)
        JPanel bottomPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        bottomPanel.setBackground(PANEL_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        
        bottomPanel.add(rockButton);
        bottomPanel.add(paperButton);
        bottomPanel.add(scissorsButton);
        bottomPanel.add(resetButton);
        bottomPanel.add(fullScreenButton);
        bottomPanel.add(new JLabel()); // Empty space for balance
        
        // Add panels to frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void attachListeners() {
        rockButton.addActionListener(this);
        paperButton.addActionListener(this);
        scissorsButton.addActionListener(this);
        resetButton.addActionListener(this);
        fullScreenButton.addActionListener(this);
    }
    
    private void toggleFullScreen() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        if (!isFullScreen) {
            // Save current window state
            normalBounds = frame.getBounds();
            frame.dispose();  // Remove window decorations
            frame.setUndecorated(true);  // Remove title bar
            gd.setFullScreenWindow(frame);  // Make full screen
            isFullScreen = true;
            fullScreenButton.setText("⬜  EXIT FULL SCREEN");
        } else {
            // Exit full screen
            gd.setFullScreenWindow(null);
            frame.dispose();
            frame.setUndecorated(false);  // Restore title bar
            frame.setBounds(normalBounds);  // Restore previous size/position
            frame.setVisible(true);
            isFullScreen = false;
            fullScreenButton.setText("🖥️  FULL SCREEN");
        }
        
        // Adjust fonts for new screen size
        adjustFontSizes(frame.getWidth(), frame.getHeight());
        
        // Revalidate layout
        frame.revalidate();
        frame.repaint();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle reset button
        if (e.getSource() == resetButton) {
            player.resetScore();
            computer.resetScore();
            updateScoreDisplay();
            resultLabel.setText("Score reset! Make a move 🎮");
            playerChoiceLabel.setText("Your choice: ❓");
            computerChoiceLabel.setText("Computer choice: ❓");
            return;
        }
        
        // Handle full screen toggle
        if (e.getSource() == fullScreenButton) {
            toggleFullScreen();
            return;
        }
        
        // Determine player choice from button clicked
        String playerChoice = "";
        if (e.getSource() == rockButton) {
            playerChoice = "rock";
        } else if (e.getSource() == paperButton) {
            playerChoice = "paper";
        } else if (e.getSource() == scissorsButton) {
            playerChoice = "scissors";
        }
        
        // Update player choice display with emoji
        player.setCurrentChoice(playerChoice);
        playerChoiceLabel.setText("Your choice: " + player.getChoiceEmoji());
        
        // Get and display computer choice
        String computerChoice = logic.getComputerChoice();
        computer.setCurrentChoice(computerChoice);
        computerChoiceLabel.setText("Computer choice: " + computer.getChoiceEmoji());
        
        // Determine winner
        String winner = logic.determineWinner(playerChoice, computerChoice);
        
        // Update score and show result message
        if (winner.equals("player")) {
            player.addScore();
            resultLabel.setText(logic.getResultMessage(winner, playerChoice, computerChoice));
        } else if (winner.equals("computer")) {
            computer.addScore();
            resultLabel.setText(logic.getResultMessage(winner, playerChoice, computerChoice));
        } else {
            resultLabel.setText(logic.getResultMessage(winner, playerChoice, computerChoice));
        }
        
        // Update score display
        updateScoreDisplay();
    }
    
    private void updateScoreDisplay() {
        scoreLabel.setText("Score: You " + player.getScore() + " - " + computer.getScore() + " Computer");
    }
}