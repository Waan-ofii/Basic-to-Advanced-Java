import java.io.*;

public class read_write {
    public static void main(String[] args) throws IOException {

        FileWriter wrote = new FileWriter("firstj.txt");
        wrote.write("hello java");
        wrote.append("\nhow is cpp\nis it bettr than you");

        wrote.close();

        FileReader readr = new FileReader("firstj.txt");

        int data = readr.read();

        while (data != -1) {
            System.out.print((char) data);
            data = readr.read();
        }
        readr.close();
    }
}