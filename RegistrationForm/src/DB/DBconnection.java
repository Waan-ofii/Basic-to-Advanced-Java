package db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBconnection {
    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/one_db",
                "root",
                ""
            );
            return con;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}