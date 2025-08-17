import java.io.File;

public class file1 {
    public static void main(String[] args) {
        File file1=new File("firstj.txt");
        if(file1.exists()) {
            System.out.println("file is exist!");
            System.out.println(file1.getPath());
            System.out.println(file1.getAbsoluteFile());
            System.out.println(file1.getAbsolutePath());
            System.out.println(file1.isFile());
            System.out.println(file1.getName());
            System.out.println(file1.length());
            System.out.println(file1.canExecute());
            System.out.println(file1.isAbsolute());

    }
        else {
            System.out.println("file does not exist!");
        }
    }
}
