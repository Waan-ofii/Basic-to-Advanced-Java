import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class Frames extends JFrame {
    JTextField txtUsername, txtEmail;
    JPasswordField txtPassword, txtConfirmPassword;
    JButton btnRegister;

    public Frames() {

        setTitle("Registration window");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        Container c = getContentPane();
        c.setBackground(Color.YELLOW);

        // Heading
        JLabel heading = new JLabel("REGISTRATION FORM");
        heading.setFont(new Font("Serif", Font.BOLD, 24));
        heading.setBounds(120, 40, 400, 30);
        add(heading);

        // Labels
        JLabel Username = new JLabel("Username");
        Username.setFont(new Font("Roboto", Font.PLAIN, 18));
        Username.setBounds(50, 100, 250, 30);
        add(Username);

        JLabel Email = new JLabel("Email");
        Email.setFont(new Font("Roboto", Font.PLAIN, 18));
        Email.setBounds(50, 150, 250, 30);
        add(Email);

        JLabel Password = new JLabel("Password");
        Password.setFont(new Font("Roboto", Font.PLAIN, 18));
        Password.setBounds(50, 200, 250, 30);
        add(Password);

        JLabel Cpassword = new JLabel("Confirm Password");
        Cpassword.setFont(new Font("Roboto", Font.PLAIN, 18));
        Cpassword.setBounds(50, 250, 250, 30);
        add(Cpassword);

        // TextFields
        txtUsername = new JTextField();
        txtUsername.setBounds(255, 100, 200, 30);
        add(txtUsername);

        txtEmail = new JTextField();
        txtEmail.setBounds(255, 150, 200, 30);
        add(txtEmail);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(255, 200, 200, 30);
        add(txtPassword);

        txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setBounds(255, 250, 200, 30);
        add(txtConfirmPassword);

        // Button
        btnRegister = new JButton("Submit");
        btnRegister.setFont(new Font("Roboto", Font.BOLD, 18));
        btnRegister.setBounds(150, 330, 150, 30);
        btnRegister.setBackground(Color.gray);
        add(btnRegister);

        
        btnRegister.addActionListener(e -> registerUser());
    }

   
    private void registerUser() {

        String name = txtUsername.getText();
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
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
    }
}