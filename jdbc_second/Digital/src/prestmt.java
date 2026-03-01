import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class prestmt {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/digitaldevice";
        String username = "root";
        String password = "12@one12ONE";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url,username,password);
            String sql = "select * from Phones where ram = '16GB'";
            PreparedStatement ps = con.prepareStatement(sql);
            
            ResultSet rs=ps.executeQuery();
          while(rs.next()){
            System.out.println(rs.getString("name") +" "+rs.getInt("version")+" "+rs.getString("ram")+" "+rs.getString("rom"));
          }  

            
        }catch(Exception e){
          System.out.println("oh shit here we go again!!");
        }
    }
}
