import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.Container;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Frames {
    public static void main(String[] args) {
        JFrame frame=new JFrame("Registration window");
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setBounds(100,100,600,600);
        

        Container c=frame.getContentPane();
         c.setBackground(Color.YELLOW);

         JLabel  heading,Username,Email,Password,Cpassword;
        
         //heading
         heading= new JLabel("REGISTRATION FORM");
         heading.setFont(new Font("Serif",Font.BOLD,24));
         heading.setBounds(120,40,400,30);
    frame.add(heading);
     
    Username=new JLabel("Username");
    Username. setFont(new Font("Roboto",Font.PLAIN,18));
    Username.setBounds(50,100,250,30);
frame.add(Username);

    Email=new JLabel("Email");
    Email. setFont(new Font("Roboto",Font.PLAIN,18));
    Email.setBounds(50,150,250,30);
frame.add(Email);

    Password=new JLabel("Password");
    Password. setFont(new Font("Roboto",Font.PLAIN,18));
    Password.setBounds(50,200,250,30);
frame.add(Password);

  Cpassword=new JLabel("Confirm Password");
    Cpassword. setFont(new Font("Roboto",Font.PLAIN,18));
    Cpassword.setBounds(50,250,250,30);
frame.add(Cpassword);

JTextField txtUsername,txtEmail;
 txtUsername= new JTextField();
txtUsername.setBounds(255,100,200,30);
txtEmail= new JTextField();
txtEmail.setBounds(255,150,200,30);

frame.add(txtUsername);
frame.add(txtEmail);
JPasswordField txtPassword,txtConfirmPassword;

txtPassword=new JPasswordField();
txtPassword.setBounds(255,200,200,30);

txtConfirmPassword=new JPasswordField();
txtConfirmPassword.setBounds(255,250,200,30);
frame.add(txtPassword);
frame.add(txtConfirmPassword);

    JButton btn=new JButton("Submit");
    btn. setFont(new Font("Roboto",Font.BOLD,18));
    btn.setBounds(150,330,150,30);
    btn.setBackground(Color.gray);

    frame.add(btn);

    }
}
