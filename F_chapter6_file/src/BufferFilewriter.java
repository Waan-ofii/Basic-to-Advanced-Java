import java.io.*;
public class BufferFilewriter {
    public static void main(String[] args) {
        try(BufferedWriter bw=new BufferedWriter(new FileWriter("BufferFile.txt")))
        {
            bw.write("this is buffer file ");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
//to read file from buffered

        try(BufferedReader br=new BufferedReader(new FileReader("BufferFile.txt"))) {
String data;
data= br.readLine();
while(data!=null){
    System.out.println(data);
    data= br.readLine();

}

        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
