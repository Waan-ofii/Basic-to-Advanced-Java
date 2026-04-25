import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;

public class Frames extends JFrame {

    JTextField txtUsername, txtEmail;
    JPasswordField txtPassword, txtConfirmPassword;
    JButton btnRegister;

    public Frames() {

        setTitle("Registration Form");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        setLayout(new GridLayout(6, 2, 10, 10));

        // Components
        add(new JLabel("Username:"));
        txtUsername = new JTextField();
        add(txtUsername);

        add(new JLabel("Email:"));
        txtEmail = new JTextField();
        add(txtEmail);

        add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        add(txtPassword);

        add(new JLabel("Confirm Password:"));
        txtConfirmPassword = new JPasswordField();
        add(txtConfirmPassword);

        btnRegister = new JButton("Register");
        add(new JLabel()); // empty cell
        add(btnRegister);

        btnRegister.addActionListener(e -> registerUser());
    }

    // ================= DATABASE CONNECTION =================
    public static Connection getConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/testdb";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    // ================= PASSWORD HASHING =================
    public static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());

        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

    // ================= REGISTRATION LOGIC =================
    private void registerUser() {

        String name = txtUsername.getText();
        String email = txtEmail.getText();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Invalid email format!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        try (Connection con = getConnection()) {

            // Check duplicate email
            String checkQuery = "SELECT * FROM users WHERE email = ?";
            try (PreparedStatement checkPs = con.prepareStatement(checkQuery)) {
                checkPs.setString(1, email);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Email already exists!");
                    return;
                }
            }

            // Insert user
            String query = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(query)) {

                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, hashPassword(password));

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Registration Successful!");

            // Clear fields
            txtUsername.setText("");
            txtEmail.setText("");
            txtPassword.setText("");
            txtConfirmPassword.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // ================= MAIN METHOD =================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Frames().setVisible(true));
    }
}