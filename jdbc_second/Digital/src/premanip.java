import java.sql.*;

public class premanip {
       public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/digitaldevice";
        String username = "root";
        String password = "12@one12ONE";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url,username,password);
            String sql = "Insert into phones(name,version,ram,rom) values(?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
             ps.setString(1,"oppo");
             ps.setInt(2,19);
             ps.setString(3,"8GB");
             ps.setString(4,"64GB");
             
             ps.executeUpdate();

             ps.setString(1,"Xanomi");
             ps.setInt(2,18);
             ps.setString(3,"16GB");
             ps.setString(4,"128GB");
             
             ps.executeUpdate();

             ps.setString(1,"Huwei");
             ps.setInt(2,24);
             ps.setString(3,"8GB");
             ps.setString(4,"64GB");
             
             int rows = ps.executeUpdate();

             System.out.println("affected: " + rows);


ps.close();
con.close();
           

            
        }catch(Exception e){

        }
    }  
    
}
