import java.sql.*;

public class BatchUpd {
        public static void main(String[] args) {
         String url = "jdbc:mysql://localhost:3306/digitaldevice";
        String username = "root";
        String password = "12@one12ONE";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        Connection con= DriverManager.getConnection(url,username,password);
        
        String query = "INSERT INTO Phones(name,version,ram,rom) VALUES (?,?,?,?)";
PreparedStatement pstatement= con.prepareStatement(query);
  pstatement.clearBatch();

pstatement.setString(1, "Itel");
pstatement.setInt(2, 8);
pstatement.setString(3, "8GB");
pstatement.setString(4, "64GB");

pstatement.addBatch();

pstatement.setString(1, "I Ultra");
pstatement.setInt(2, 6);
pstatement.setString(3, "14GB");
pstatement.setString(4, "128GB");

pstatement.addBatch();

int[] count = pstatement.executeBatch();
boolean allGoods = true;

for(int i =0; i<count.length; i++){
    if(count[i]!=1){
        allGoods = false;
    }
}
if(allGoods){
    System.out.println("all data are inserted succesfully");
}else{
    System.out.println("something error  ocurred");
}

        pstatement.close();
        con.close();
        } catch (Exception e) {
            e.printStackTrace();;
        }
       
    }
}

