import java.awt.*;
import javax.swing.*;

public class TextArea {
    public static void main(String[] args) {

        JFrame fr=new JFrame("T code");

        fr.setLayout(null);
        JTextArea ta = new JTextArea("Write area.....");
        ta.setBounds(50, 20, 500, 150);
        ta.setFont(new Font("serif", Font.BOLD, 22));
        ta.setBackground(Color.DARK_GRAY);
        ta.setForeground(Color.WHITE);
        ta.setLineWrap(true);
        ta.setEditable(true);  // Ensure text area is editable
   ta.setLayout(null);
   fr.add(ta);
        fr.setVisible(true);
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setBounds(100,100,500,300);


        ImageIcon icon=new ImageIcon("ttt.png");
        fr.setIconImage(icon.getImage());

        Container C=fr.getContentPane();
        C.setBackground(Color.CYAN);
    }
}