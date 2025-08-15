import java.io.*;

public class bytefileReader {
    public static void main(String[] args) {
        try(FileInputStream p1=new FileInputStream("image.jpg")){
            int dubbisi= p1.read();
            while(dubbisi!=-1){

                System.out.print((char)dubbisi);
                dubbisi=p1.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
