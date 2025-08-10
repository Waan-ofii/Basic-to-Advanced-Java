import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Scnr {
    public static void main(String[] args) {
        try {
            File fil = new File("scann.txt");


            Scanner rr = new Scanner(fil);
            while (rr.hasNextLine()) {
                System.out.println(rr.nextLine());

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
