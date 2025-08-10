import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
public class LowLevelByteStreamExample {
    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("scann.txt");
             FileOutputStream fos = new FileOutputStream("scanns.txt")) {
            int byteContent;
            while ((byteContent = fis.read()) != -1) {
                fos.write(byteContent);
            }
        } catch (IOException e) {
            e.printStackTrace(); } }}
