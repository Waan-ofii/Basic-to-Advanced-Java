import java.awt.*;
import javax.swing.*;

public class Frames {
    public static void main(String[] args) {
        JFrame frame=new JFrame("T code");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //frame.setSize(500,300);
       // frame.setLocation(100,100);

        frame.setBounds(100,100,500,300);
        //frame.setName("T code");


        ImageIcon icon=new ImageIcon("ttt.png");
        frame.setIconImage(icon.getImage());

        Container C=frame.getContentPane();
        C.setBackground(Color.CYAN);
    }
}