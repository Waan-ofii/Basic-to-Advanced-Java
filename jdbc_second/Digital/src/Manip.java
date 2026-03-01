import java.sql.*;


public class Manip {
 public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/digitaldevice";
        String username = "root";
        String password = "12@one12ONE";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(url,username,password);
            Statement stmt = con.createStatement();
            String sql = "Insert Into Phones(name,version,ram,rom) values('Tecno',16,'32GB','128GB')";
            int row = stmt.executeUpdate(sql);

            System.out.println("rows affected: "+row);

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}