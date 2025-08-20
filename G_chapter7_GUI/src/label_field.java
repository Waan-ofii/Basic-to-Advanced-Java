import javax.swing.*;
import java.awt.*;

public class label_field {
    public static void main(String[] args) {
        JFrame frame = new JFrame("T code");
        JLabel l1, l2, l3;

        l1 = new JLabel("CHAANNALLI BARNOOTA TEKNOLOOJI");
        l1.setFont(new Font("TT", Font.BOLD, 22));
        l1.setBounds(100, 10, 500, 30);
        l1.setForeground(Color.BLACK);

        l2 = new JLabel("Username :");
        l2.setFont(new Font("TT", Font.BOLD, 22));
        l2.setBounds(50, 40, 175, 28);
        l2.setForeground(Color.BLACK);

        l3 = new JLabel("Password :");
        l3.setFont(new Font("TT", Font.BOLD, 22));
        l3.setBounds(50, 80, 175, 28);
        l3.setForeground(Color.BLACK);

        JTextField t1 = new JTextField();
        t1.setBounds(210, 40, 180, 30);

        JTextField t2 = new JTextField();
        t2.setBounds(210, 80, 180, 30);

        frame.add(t1);
        frame.add(l1);
        frame.add(l2);

        frame.add(t2);
        frame.add(l3);



        frame.setVisible(true);
        frame.setLayout(null);  // Layout manager set to null for absolute positioning
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(500, 200, 500, 300);

        // Set an icon for the JFrame (ensure the path is correct)
        ImageIcon icon = new ImageIcon("ttt.png");
        frame.setIconImage(icon.getImage());

        // Set the background color of the frame
        Container C = frame.getContentPane();
        C.setBackground(Color.CYAN);
    }
}
