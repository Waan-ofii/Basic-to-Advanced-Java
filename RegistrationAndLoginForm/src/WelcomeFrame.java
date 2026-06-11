import javax.swing.*;
import java.awt.*;

public class WelcomeFrame extends JFrame {

    public WelcomeFrame(String username) {

        setTitle("Welcome");

        setSize(1200,700);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(
                new Color(255,248,220));

        setLayout(new BorderLayout());

        JLabel welcome =
                new JLabel(
                        "<html><center>Welcome "
                                + username
                                + "!<br><br>A New Day, A New Opportunity.</center></html>",
                        SwingConstants.CENTER);

        welcome.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        32));

        welcome.setForeground(
                new Color(255,140,0));

        add(welcome,
                BorderLayout.CENTER);

        JButton logout =
                new JButton("Logout");

        logout.setBackground(
                new Color(220,53,69));

        logout.setForeground(
                Color.WHITE);

        logout.addActionListener(e -> {

            dispose();

            new LoginFrame().setVisible(true);
        });

        JPanel panel =
                new JPanel();

        panel.setOpaque(false);

        panel.add(logout);

        add(panel,
                BorderLayout.SOUTH);
    }
}