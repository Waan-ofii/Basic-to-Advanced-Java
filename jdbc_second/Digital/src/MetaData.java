import java.sql.*;


public class MetaData {
    public static void main(String[] args) throws Exception {
       
        String url = "jdbc:mysql://localhost:3306/digitaldevice";
        String username = "root";
        String password = "12@one12ONE";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(url,username,password);
            

        DatabaseMetaData dm= con.getMetaData();
        System.out.println("==========Database MetaData ================");
        System.out.println();
 System.out.println("database URL : " + dm.getURL());
System.out.println("database username : " + dm.getUserName());
System.out.println("database product name : " + dm.getDatabaseProductName());
System.out.println("database product version : " + dm.getDatabaseProductVersion());
System.out.println("JDBC driver name : " + dm.getDriverName());
System.out.println("JDBC driver version : " + dm.getDriverVersion());
System.out.println("Max number of connections : " + dm.getMaxConnections());
System.out.println("MaxTableNameLength : " + dm.getMaxTableNameLength());
System.out.println("MaxColumnsInTable : " + dm.getMaxColumnsInTable());
System.out.println();

System.out.println("=========RresultSet MetaData ================");
 
String sql = "select * from phones";
 Statement stmt = con.createStatement();
 ResultSet rs = stmt.executeQuery(sql);
ResultSetMetaData rm =rs.getMetaData();

System.out.println("get column count : "+rm.getColumnCount());
System.out.println("is column auto increament : "+rm.isAutoIncrement(4));
System.out.println("get column name : "+rm.getColumnName(2));
System.out.println("get column Label: "+rm.getColumnLabel(3));
System.out.println("get Table name: "+rm.getTableName(3));
System.out.println("get column Type: "+rm.getColumnType(2));
System.out.println("get is writable: "+rm. isWritable(4));
System.out.println();
con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
