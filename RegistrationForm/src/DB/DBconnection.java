package db;  // make sure this matches the folder name (db lowercase)

import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnection {
    public static Connection getConnection() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");  

            // Connect to database
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydatab", // your DB name
                "root",                                // username
                "12@one12ONE"                                      // password
            );
            return con;

        } catch (Exception e) {
            e.printStackTrace();  // IMPORTANT: shows the real reason
            return null;          // returning null here causes con to be null
        }
    }
}