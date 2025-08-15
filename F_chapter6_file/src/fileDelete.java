import java.io.*;
public class fileDelete {
    public static void main(String[] args) {
        try {
            //to delete file
           // File myObj = new File("example.txt");
            //to delete folder
            File myObj = new File("C:\\Antro");
            if (myObj.delete()) {
                System.out.println("Deleted the file or folder: " + myObj.getName());
            } else {
                System.out.println("Failed to delete the file.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
