import java.sql.Connection;
import java.sql.DriverManager;


public class DBconnection {

    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/registration_db";
            String user = "root";
            String password = "12@one12ONE";

            Connection con = DriverManager.getConnection(url, user, password);
            return con;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}