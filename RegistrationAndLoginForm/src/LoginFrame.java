import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    JTextField txtEmail;
    JPasswordField txtPassword;

    public LoginFrame() {

        setTitle("Login");
        setSize(1200,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(245,245,245));
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(450,400));
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);

        JLabel title = new JLabel("User Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBounds(140,30,200,40);
        panel.add(title);

        JLabel lblEmail = new JLabel("Email Address:");
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblEmail.setBounds(50,120,120,30);
        panel.add(lblEmail);

        txtEmail = new JTextField();
        txtEmail.setBounds(180,120,220,30);
        panel.add(txtEmail);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPassword.setBounds(50,180,120,30);
        panel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(180,180,220,30);
        panel.add(txtPassword);

        JCheckBox show = new JCheckBox("Show Password");
        show.setBounds(180,220,150,25);
        show.setBackground(Color.WHITE);
        show.addActionListener(e -> {
            if(show.isSelected()) {
                txtPassword.setEchoChar((char)0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });
        panel.add(show);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(150,270,150,40);
        btnLogin.setBackground(new Color(40,167,69));
        btnLogin.setForeground(Color.WHITE);
        panel.add(btnLogin);

        JLabel registerLink = new JLabel("<html><u>Don't have an account? Register</u></html>");
        registerLink.setBounds(100,330,250,30);
        registerLink.setForeground(Color.BLUE);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
        });
        panel.add(registerLink);

        btnLogin.addActionListener(e -> login());
        add(panel);
    }

    private void login() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE email=?");
            ps.setString(1, txtEmail.getText());
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                String dbHash = rs.getString("password");
                String enteredHash = PasswordUtil.hashPassword(new String(txtPassword.getPassword()));
                
                if(dbHash.equals(enteredHash)) {
                    dispose();
                    new WelcomeFrame(rs.getString("name")).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Password");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Account not found");
            }
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}


