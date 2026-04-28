import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            String url = "jdbc:mysql://localhost:3306/registration_db";
            String user = "root";
            String password = "12@one12ONE";

            return DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}