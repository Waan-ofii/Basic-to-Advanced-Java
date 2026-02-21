// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.ResultSet;

import java.sql.*;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/school_db","root","12@one12ONE");
            Statement stat = con.createStatement();
        /*
        //to manipulate data
        String sql= "insert into students(id, name,age,grade) values(3,'will smith',21 ,'A-')";
            int ru = stat.executeUpdate(sql);
             System.out.println(ru);
         */
         /* */
                //   how to get metedata wich means data about our database
                String sq = "select * from students";
                //to display data
                ResultSet rs = stat.executeQuery(sq);
            ResultSetMetaData Rmt = rs.getMetaData();
           int count = Rmt.getColumnCount();
            while (rs.next()) {
                for(int i=1; i<=count; i++){
             System.out.print(rs.getString(i)+"\t");  
                }
                System.out.println();
            } 
            con.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
