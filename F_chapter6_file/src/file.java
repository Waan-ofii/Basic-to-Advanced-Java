import java.io.File;

public class file {
    public static void main(String[] args) {
        File file=new File("C:\\proj 1\\filj.txt");
        if(file.exists()){
            System.out.println("file is exist !");
            System.out.println("path :"+file.getPath());
            System.out.println("Absolute path :"+file.getAbsolutePath());
            System.out.println("length :"+file.length());
            System.out.println("file name :"+file.getName());
            System.out.println("is file readable  :"+file.canRead());
            System.out.println("file Writable :"+file.canWrite());
        }else {
            System.out.println("file does not exist !!");
        }
    }
}