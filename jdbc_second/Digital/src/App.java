import java.sql.*;

public class App {
    public static void main(String[] args) throws Exception {
       
        String url = "jdbc:mysql://localhost:3306/digitaldevice";
        String username = "root";
        String password = "12@one12ONE";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(url,username,password);
            Statement stmt = con.createStatement();
            String sql = "select * from phones";
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()){
                System.out.println(rs.getString("name")+" "+rs.getInt("version")+" "+rs.getString("ram")+" "+rs.getString("rom"));
            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
