import ui.TodoFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set Look and Feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            TodoFrame frame = new TodoFrame();
            frame.setVisible(true);
        });
    }
}