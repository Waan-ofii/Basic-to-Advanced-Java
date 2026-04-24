import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
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

    public static void main(String[] args) {
    Connection con = DBConnection.getConnection();

    if (con != null) {
        System.out.println("Connected successfully!");
    } else {
        System.out.println("Connection failed!");
    }
}
}