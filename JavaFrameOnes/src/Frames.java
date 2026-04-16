import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
public class Frames {
    public static void main(String[] args) {
        
        JFrame frame= new JFrame("Form Window");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      //  frame.setSize(500,300);
      //  frame.setLocation(100,100);
      frame.setLayout(null);
      frame.setBounds(100,100,700,700);
     // frame.setTitle("Form window");
     ImageIcon icon = new ImageIcon("ju-logo.png");
      frame.setIconImage(icon.getImage());

      Container c=frame.getContentPane();
      c.setBackground(Color.YELLOW);

      JLabel l1,l2,l3;
      l1=new JLabel("Registratin Form");
      l1.setFont(new Font("Roboto",Font.BOLD,28));
      l1.setBounds(100,20,400,30);
      l1.setForeground(Color.BLACK);
frame.add(l1);

   l2=new JLabel("Username: ");
      l2.setFont(new Font("Roboto",Font.BOLD,28));
      l2.setBounds(50,60,175,30);
      l2.setForeground(Color.BLACK);
frame.add(l2);

  l3=new JLabel("Password: ");
      l3.setFont(new Font("Roboto",Font.BOLD,28));
      l3.setBounds(50,100,175,30);
      l3.setForeground(Color.BLACK);
frame.add(l3);

JTextField t1= new JTextField();
t1.setBounds(200,60,150,30);
JTextField t2= new JTextField();
t2.setBounds(200,100,150,30);

frame.add(t1);
frame.add(t2);

JTextArea ta=new JTextArea("write here....");
ta.setBounds(50,150,200,250);
ta.setBackground(Color.darkGray);
ta.setFont(new Font("serif",Font.BOLD,22));
ta.setForeground(Color.white);
ta.setLineWrap(true);
 frame.add(ta);


    }
    
}
