import java.io.*;


public class filewr {
    public static void main(String[] args) {
        try (FileWriter pp2 = new FileWriter("characterFile.txt")) {

            pp2.write("this is character stream ");
            System.out.println("text is written to file succesfully");
            System.out.println("here is your text : ");


        } catch (IOException e) {
            e.printStackTrace();
        }


        try (FileReader pp2R = new FileReader("characterFile.txt")) {

            int data = pp2R.read();
            while (data != -1) {
                System.out.print((char) data);
                data = pp2R.read();

            }
        } catch(Exception e){
                e.printStackTrace();
            }



    }
}




