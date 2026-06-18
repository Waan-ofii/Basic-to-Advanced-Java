import java.sql.*;

public class TestDb {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/campusshare";
        String user = "root";
        String pass = "biko123!";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                System.out.println("Connection successful!");
                String sql = "SELECT * FROM user";
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql)) {
                    System.out.println("--------------------------------------------------");
                    System.out.printf("%-5s | %-15s | %-10s | %-10s | %s\n", "ID", "Username", "Role", "IsActive",
                            "Password Hash");
                    System.out.println("--------------------------------------------------");
                    while (rs.next()) {
                        System.out.printf("%-5d | %-15s | %-10s | %-10d | %s\n",
                                rs.getInt("user_id"),
                                rs.getString("username"),
                                rs.getString("role"),
                                rs.getInt("is_active"),
                                rs.getString("password"));
                    }
                    System.out.println("--------------------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
