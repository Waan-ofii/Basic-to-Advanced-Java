package com.university.server;

import java.sql.*;
import java.io.*;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;
    private static String url;
    private static String username;
    private static String password;

    static {
        try {
            Properties props = new Properties();
            // Try multiple locations for config.properties
            String[] paths = {"config.properties", "src/config.properties", "../config.properties"};
            FileInputStream fis = null;

            for (String path : paths) {
                File file = new File(path);
                if (file.exists()) {
                    fis = new FileInputStream(file);
                    break;
                }
            }

            if (fis == null) {
                System.err.println("config.properties not found! Using defaults.");
                url = "jdbc:mysql://localhost:3306/university_db";
                username = "root";
                password = "";
            } else {
                props.load(fis);
                fis.close();
                url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/university_db");
                username = props.getProperty("db.username", "root");
                password = props.getProperty("db.password", "");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading config.properties: " + e.getMessage());
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

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            System.out.println("✅ Database connected successfully!");
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    public static int executeUpdate(String query, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        int affectedRows = stmt.executeUpdate();

        if (query.trim().toUpperCase().startsWith("INSERT")) {
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return affectedRows;
    }

    public static ResultSet executeQuery(String query, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }
}