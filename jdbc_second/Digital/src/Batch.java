import java.sql.*;


public class Batch {
    public static void main(String[] args) {
         String url = "jdbc:mysql://localhost:3306/digitaldevice";
        String username = "root";
        String password = "12@one12ONE";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        Connection con= DriverManager.getConnection(url,username,password);
        Statement stmt = con.createStatement();
        stmt.clearBatch();

        stmt.addBatch("Insert into phones values('T Spark',22,'8GB','128GB')");
        stmt.addBatch("Insert into phones values('T Common',24,'16GB','256GB')");


        int[] counts = stmt.executeBatch();
        boolean allGood = true;

        for(int i=0; i<counts.length; i++){
            if(counts[i] !=1){
                allGood=false;
            }
        }
        if(allGood){
            System.out.println("all data are inserted");
        }else{
            System.out.println("at leats one error are accured");
        }
        stmt.close();
        con.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
       
    }
}
