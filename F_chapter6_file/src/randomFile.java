import java.io.*;

public class randomFile {
    public static void main(String[] args) {
        try {
            RandomAccessFile raf = new RandomAccessFile("data.txt", "rw");
            //write in random file
            raf.writeUTF("hello world");
            raf.writeInt(2025);
            raf.seek(0);
            //read fromrandom file

            String data=raf.readUTF();
            int year=raf.readInt();
            System.out.println("data : "+data);
            System.out.println("year : "+year);
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
