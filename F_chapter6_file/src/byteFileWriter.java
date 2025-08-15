import java.io.*;


public class byteFileWriter {
    public static void main(String[] args) {

        try(FileOutputStream pp1=new FileOutputStream("tryFILe.txt",true)){
            String content="\ncat \n ";
            String addcont="dog\n";
            pp1.write(content.getBytes());
            pp1.write(addcont.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
