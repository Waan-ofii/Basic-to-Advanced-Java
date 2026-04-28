import java.awt.Font;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.awt.Color;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Frames extends JFrame{

 JTextField txtUsername, txtEmail;
    JPasswordField txtPassword, txtConfirmPassword;
    JButton btnRegister;


public Frames(){
    setTitle("Registration window");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100,100,700,700);
    setLayout(null);

    Container c= getContentPane();
    c.setBackground(Color.LIGHT_GRAY);

     JLabel Header ,Username , Email ,Password, ConfirmPassword;

    Header=new JLabel("Registration Form");
    Header.setFont(new Font("Roboto",Font.BOLD,28));
    Header.setBounds(150,20,400,30);
    Header.setForeground(Color.BLACK);
    add(Header);

    Username = new JLabel("Username");
    Username.setFont(new Font("Serif",Font.BOLD,20));
    Username.setBounds(50,60,175,30);
    Username.setForeground(Color.BLACK);

    Email = new JLabel("Email");
    Email.setFont(new Font("Serif",Font.BOLD,20));
    Email.setBounds(50,100,175,30);
    Email.setForeground(Color.BLACK);

    Password = new JLabel("Password");
    Password.setFont(new Font("Serif",Font.BOLD,20));
    Password.setBounds(50,140,175,30);
    Password.setForeground(Color.BLACK);

    ConfirmPassword = new JLabel("ConfirmPassword");
    ConfirmPassword.setFont(new Font("Serif",Font.BOLD,20));
    ConfirmPassword.setBounds(50,180,175,30);
    ConfirmPassword.setForeground(Color.BLACK);

    add(Username);
    add(Email);
    add(Password);
    add(ConfirmPassword);

txtUsername= new JTextField();
txtUsername.setBounds(240,60,220,30);
txtUsername.setForeground(Color.BLACK);

txtEmail= new JTextField();
txtEmail.setBounds(240,100,220,30);
txtEmail.setForeground(Color.BLACK);

    txtPassword=  new JPasswordField();
    txtPassword.setBounds(240,140,220,30);

    txtConfirmPassword=  new JPasswordField();
    txtConfirmPassword.setBounds(240,180,220,30);

    btnRegister = new JButton("Submit");
    btnRegister.setFont(new Font("Roboto", Font.BOLD, 18));
    btnRegister.setBounds(150, 230, 150, 30);
    btnRegister.setBackground(Color.gray);
     
    add(txtUsername);
    add(txtEmail);
    add(txtPassword);
    add(txtConfirmPassword);
    add(btnRegister);
    
btnRegister.addActionListener(e -> registerUser());
}


private void registerUser(){
    String name=txtUsername.getText();
    String email=txtEmail.getText();
    String password = new String(txtPassword.getPassword());
   String confirmPassword = new String(txtConfirmPassword.getPassword());

   if(name.isEmpty()|| email.isEmpty()|| password.isEmpty()|| confirmPassword.isEmpty()){
    JOptionPane.showMessageDialog(this,"All Fields are Required");
    return;
   }

    if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(this, "Passwords do not match!");
        return;
    }

    try {
         Connection con = DBConnection.getConnection();

            String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration Successful!");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
}



    
    public static void main(String[] args) {
       new Frames().setVisible(true);
       
         Connection con = DBConnection.getConnection();

    if (con != null) {
        System.out.println("Connected successfully!");
    } else {
        System.out.println("Connection failed!");
    }

//         JFrame fr=new JFrame("registration window");
//         fr.setVisible(true);
//         fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         fr.setBounds(100,100,700,700);
//         fr.setLayout(null);

//         Container c=fr.getContentPane();
//         c.setBackground(Color.LIGHT_GRAY);

//         JLabel Header ,Username , Email ,Password, ConfirmPassword;

//     Header=new JLabel("Registration Form");
//     Header.setFont(new Font("Roboto",Font.BOLD,28));
//     Header.setBounds(150,20,400,30);
//     Header.setForeground(Color.BLACK);
// fr.add(Header);
    
//         Username = new JLabel("Username");
//         Username.setFont(new Font("Serif",Font.BOLD,20));
//         Username.setBounds(50,60,175,30);
//         Username.setForeground(Color.BLACK);

//         Email = new JLabel("Email");
//         Email.setFont(new Font("Serif",Font.BOLD,20));
//         Email.setBounds(50,100,175,30);
//         Email.setForeground(Color.BLACK);

//         Password = new JLabel("Password");
//         Password.setFont(new Font("Serif",Font.BOLD,20));
//         Password.setBounds(50,140,175,30);
//         Password.setForeground(Color.BLACK);

//         ConfirmPassword = new JLabel("ConfirmPassword");
//         ConfirmPassword.setFont(new Font("Serif",Font.BOLD,20));
//         ConfirmPassword.setBounds(50,180,175,30);
//         ConfirmPassword.setForeground(Color.BLACK);

//         fr.add(Username);
//         fr.add(Email);
//         fr.add(Password);
//         fr.add(ConfirmPassword);

//         JTextField txtUsername,txtEmail;
//         JPasswordField txtPassword, txtConfirmPassword;

//         txtUsername= new JTextField();
//         txtUsername.setBounds(240,60,220,30);
//         txtUsername.setForeground(Color.BLACK);

//         txtEmail= new JTextField();
//         txtEmail.setBounds(240,100,220,30);
//         txtEmail.setForeground(Color.BLACK);

//         txtPassword=  new JPasswordField();
//         txtPassword.setBounds(240,140,220,30);

//         txtConfirmPassword=  new JPasswordField();
//         txtConfirmPassword.setBounds(240,180,220,30);
        
//         fr.add(txtUsername);
//         fr.add(txtEmail);
//         fr.add(txtPassword);
//         fr.add(txtConfirmPassword);

//         JButton btn = new JButton("Submit");
//         btn.setBounds(240,250,120,30);
//         fr.add(btn);


    }
}
