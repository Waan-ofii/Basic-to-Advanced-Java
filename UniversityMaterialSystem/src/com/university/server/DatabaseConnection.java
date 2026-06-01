package com.university.server;

import java.sql.*;
import java.io.*;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;
    private static String url;
    private static String username;
    private static String password;
    
    // Load configuration from config.properties file
    static {
        try {
            Properties props = new Properties();
            FileInputStream fis = new FileInputStream("config.properties");
            props.load(fis);
            fis.close();
            
            url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/university_db");
            username = props.getProperty("db.username", "root");
            password = props.getProperty("db.password", "12@one12ONE");
            
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("config.properties file not found! Using default values.");
            url = "jdbc:mysql://localhost:3306/university_db";
            username = "root";
            password = "";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    // Get database connection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }
    
    // Close connection
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Test connection
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            System.out.println("✅ Database connected successfully!");
            System.out.println("URL: " + url);
            System.out.println("User: " + username);
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }
    
    // Execute update (INSERT, UPDATE, DELETE)
    public static int executeUpdate(String query, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        int affectedRows = stmt.executeUpdate();
        
        // Return generated ID if it's an INSERT
        if (query.trim().toUpperCase().startsWith("INSERT")) {
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return affectedRows;
    }
    
    // Execute query (SELECT)
    public static ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }
}