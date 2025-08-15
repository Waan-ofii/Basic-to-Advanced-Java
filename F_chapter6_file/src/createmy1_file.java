import java.io.File;
import java.io.IOException;

public class createmy1_file {
    public static void main(String[] args) {
        File f1=new File("Binaryfile.dat");
        try {
            if (f1.exists()) {
                System.out.println("file is already exist");
            } else {
                f1.createNewFile();
                System.out.println("file is created succesfully  !!!!!!!!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        }
    }


