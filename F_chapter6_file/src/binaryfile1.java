import java.io.*;

public class binaryfile1 {
    public static void main(String[] args) {
        byte[] data={84,69,83,70,65,89,69};
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("Binaryfile.dat"))) {
            bos.write(data);
        } catch (IOException e) {
           e.printStackTrace();
        }

    try(BufferedInputStream rd=new BufferedInputStream(new FileInputStream("Binaryfile.dat"))){
        int dataw=rd.read();
        while (dataw!=-1){
            System.out.print((char)dataw);
            dataw=rd.read();
        }
    }catch(IOException e){
        e.printStackTrace();
    }
}
}