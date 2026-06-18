package dao;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/campusshare";
    private static final String USER = "root";
    private static final String PASSWORD = "12@one12ONE";

    private static Connection connection = null;

    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                runDatabasePatches(connection);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException("JDBC Driver not found!");
            }
        }
        return connection;
    }

    private static void runDatabasePatches(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Update prof_smith to correct hash of 'teacher123' if it has the incorrect hash
            stmt.executeUpdate("UPDATE user SET password = '" + UserDAO.hashPassword("teacher123") + 
                               "' WHERE username = 'prof_smith' AND password = 'cde383eee8ee7a4400adf7a15f716f179a2eb97646b37e089eb8d6d04e663416'");
            
            // Update john_student to correct hash of 'student123' if it has the incorrect hash
            stmt.executeUpdate("UPDATE user SET password = '" + UserDAO.hashPassword("student123") + 
                               "' WHERE username = 'john_student' AND password = '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b'");
        } catch (Exception e) {
            System.err.println("Database auto-patch failed: " + e.getMessage());
        }
    }

    public static synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}