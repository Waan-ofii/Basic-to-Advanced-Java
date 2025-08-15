import java.io.File;
import java.util.Scanner;

public class ggg {
    public static void main(String[] args) {
        try {
            File nn = new File("data.txt");
            Scanner bb=new Scanner(nn);
            while(bb.hasNextLine()){
                String hhh=bb.nextLine();
                System.out.println(hhh);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
