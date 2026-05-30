import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDB {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Testing Database Connection");
        System.out.println("========================================\n");
        
        // MySQL connection settings - CHANGE THESE!
        String url = "jdbc:mysql://localhost:3306/university_db";
        String username = "root";
        String password = "12@one12ONE";  // ← CHANGE THIS to your MySQL password!
        
        try {
            // Load MySQL Driver (works with MySQL 9.6.0)
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ MySQL Driver 9.6.0 loaded successfully!");
            
            // Connect to database
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connected to MySQL database!");
            System.out.println("   Database: " + conn.getCatalog());
            
            // Test query - get database version
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT VERSION()");
            if (rs.next()) {
                System.out.println("   MySQL Version: " + rs.getString(1));
            }
            
            // Check if users table exists
            System.out.println("\n📋 Checking tables...");
            rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("   Tables in database:");
            boolean hasTables = false;
            while (rs.next()) {
                hasTables = true;
                System.out.println("     - " + rs.getString(1));
            }
            
            if (!hasTables) {
                System.out.println("     (No tables found. Run schema.sql first!)");
            }
            
            // If users table exists, show users
            try {
                rs = stmt.executeQuery("SELECT * FROM users");
                System.out.println("\n👥 Users in database:");
                System.out.println("----------------------------------------");
                boolean hasUsers = false;
                while (rs.next()) {
                    hasUsers = true;
                    String id = rs.getString("student_id");
                    String name = rs.getString("name");
                    String role = rs.getString("role");
                    System.out.println("  📌 " + id + " | " + name + " | " + role);
                }
                if (!hasUsers) {
                    System.out.println("  No users found");
                }
                System.out.println("----------------------------------------");
            } catch (SQLException e) {
                System.out.println("  (users table doesn't exist yet)");
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            System.out.println("\n========================================");
            System.out.println("   ✅ DATABASE TEST COMPLETE! ✅");
            System.out.println("========================================");
            
        } catch (ClassNotFoundException e) {
            System.err.println("\n❌ ERROR: MySQL Driver not found!");
            System.err.println("   Make sure mysql-connector-j-9.6.0.jar is in the 'lib' folder");
            System.err.println("   Then restart VS Code");
            
        } catch (SQLException e) {
            System.err.println("\n❌ ERROR: Database connection failed!");
            System.err.println("   Error: " + e.getMessage());
            System.err.println("\n   Possible fixes:");
            System.err.println("   1. Is MySQL running?");
            System.err.println("      → Open Services (Windows) and check if MySQL is running");
            System.err.println("   2. Is username/password correct?");
            System.err.println("      → Current username: " + username);
            System.err.println("      → Update the password variable in the code");
            System.err.println("   3. Does database 'university_db' exist?");
            System.err.println("      → Run: CREATE DATABASE university_db;");
        }
    }
}