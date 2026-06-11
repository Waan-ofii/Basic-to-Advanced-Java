import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {

    JTextField txtUsername;
    JTextField txtEmail;
    JPasswordField txtPassword;
    JPasswordField txtConfirmPassword;

    public RegisterFrame() {

        setTitle("Registration");
        setSize(1200,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(
                new Color(240,248,255));

        setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setPreferredSize(
                new Dimension(450,500));

        panel.setBackground(Color.WHITE);

        panel.setLayout(null);

        JLabel title =
                new JLabel("Create Account");

        title.setFont(
                new Font("Segoe UI",
                        Font.BOLD,
                        26));

        title.setBounds(
                120,
                20,
                250,
                40);

        panel.add(title);

        JLabel lblUser =
                new JLabel("Username");

        lblUser.setBounds(50,100,100,30);
        panel.add(lblUser);

        txtUsername =
                new JTextField();

        txtUsername.setBounds(
                180,100,220,30);

        panel.add(txtUsername);

        JLabel lblEmail =
                new JLabel("Email");

        lblEmail.setBounds(50,150,100,30);
        panel.add(lblEmail);

        txtEmail =
                new JTextField();

        txtEmail.setBounds(
                180,150,220,30);

        panel.add(txtEmail);

        JLabel lblPassword =
                new JLabel("Password");

        lblPassword.setBounds(50,200,100,30);
        panel.add(lblPassword);

        txtPassword =
                new JPasswordField();

        txtPassword.setBounds(
                180,200,220,30);

        panel.add(txtPassword);

        JLabel lblConfirm =
                new JLabel("Confirm Password");

        lblConfirm.setBounds(50,250,120,30);
        panel.add(lblConfirm);

        txtConfirmPassword =
                new JPasswordField();

        txtConfirmPassword.setBounds(
                180,250,220,30);

        panel.add(txtConfirmPassword);

        JCheckBox show =
                new JCheckBox("Show Password");

        show.setBounds(180,290,150,25);

        show.setBackground(Color.WHITE);

        show.addActionListener(e -> {

            if(show.isSelected()) {

                txtPassword.setEchoChar((char)0);
                txtConfirmPassword.setEchoChar((char)0);

            } else {

                txtPassword.setEchoChar('•');
                txtConfirmPassword.setEchoChar('•');
            }
        });

        panel.add(show);

        JButton btnRegister =
                new JButton("Register");

        btnRegister.setBounds(
                150,340,150,40);

        btnRegister.setBackground(
                new Color(0,123,255));

        btnRegister.setForeground(
                Color.WHITE);

        panel.add(btnRegister);

        JLabel loginLink =
                new JLabel(
                        "<html><u>Already have an account? Login</u></html>");

        loginLink.setForeground(Color.BLUE);

        loginLink.setBounds(
                110,400,250,30);

        loginLink.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        loginLink.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    public void mouseClicked(
                            java.awt.event.MouseEvent e) {

                        dispose();

                        new LoginFrame().setVisible(true);
                    }
                });

        panel.add(loginLink);

        btnRegister.addActionListener(
                e -> registerUser());

        add(panel);
    }

    private void registerUser() {

        String username =
                txtUsername.getText().trim();

        String email =
                txtEmail.getText().trim();

        String password =
                new String(
                        txtPassword.getPassword());

        String confirm =
                new String(
                        txtConfirmPassword.getPassword());

        if(username.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
                confirm.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "All fields are required");

            return;
        }

        if(!password.equals(confirm)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match");

            return;
        }

        try {

            Connection con =
                    DBConnection.getConnection();

            PreparedStatement check =
                    con.prepareStatement(
                            "SELECT * FROM users WHERE email=?");

            check.setString(1,email);

            ResultSet rs =
                    check.executeQuery();

            if(rs.next()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Email already registered");

                return;
            }

            String hash =
                    PasswordUtil.hashPassword(password);

            PreparedStatement ps =
                    con.prepareStatement(
                            "INSERT INTO users(name,email,password) VALUES(?,?,?)");

            ps.setString(1,username);
            ps.setString(2,email);
            ps.setString(3,hash);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Registration Successful");

            dispose();

            new LoginFrame().setVisible(true);

        } catch(Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage());
        }
    }
}