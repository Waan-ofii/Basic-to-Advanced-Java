import java.sql.*;

public class Tran {
    public static void main(String[] args) throws Exception {
       
        String url = "jdbc:mysql://localhost:3306/digitaldevice";
        String username = "root";
        String password = "12@one12ONE";
           Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
           con= DriverManager.getConnection(url,username,password);
            //allow transaction
con.setAutoCommit(false);
Statement stmt = con.createStatement();
String SQL = "INSERT INTO Phones VALUES ('SMYDEL', 3, '4GB','32GB')";
stmt.executeUpdate(SQL);
String SQ = "INSERT INTO Phones VALUES ('LG', 8, '6GB','64GB')";
stmt.executeUpdate(SQ);
//execute now
con.commit();
}catch(SQLException e){
con.rollback();}
    }
}
