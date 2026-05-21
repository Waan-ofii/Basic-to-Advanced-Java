package src.com.rpsgame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // SwingUtilities.invokeLater ensures GUI runs on the Event Dispatch Thread
        // This is best practice for Swing applications
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameGUI();  // Create and show the game
            }
        });
    }
}