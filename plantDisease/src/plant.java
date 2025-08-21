import java.io.*;
import java.util.*;

public class plant {
    String DiseaseName;
    String Sympotoms;
    String Treatnent;
    int count=0;

   /*plant(String dn,String Sp,String Tr){
       this.DiseaseName=dn;
       this.Sympotoms=Sp;
       this.Treatnent=Tr;
   }

    public String getDiseaseName() {
        return DiseaseName;
    }

    public String getSympotoms() {
        return Sympotoms;
    }

    public String getTreatnent() {
        return Treatnent;
    }
*/

    void AddDisease(){
       Scanner input=new Scanner(System.in);
        System.out.print("please enter disease name : ");
        DiseaseName=input.nextLine();
        System.out.print("please enter disease Sympototms : ");
        Sympotoms=input.nextLine();
        System.out.print("please enter disease Treatment : ");
        Treatnent=input.nextLine();
        count++;
    }
   /* void SaveDiseaseTofile() {
        try (FileOutputStream sf = new FileOutputStream("disease.txt")) {


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }*/
    void dispalyDisease(){
        if(count==0){
            System.out.println(" file is empty");
        }else{
        for(int i=0; i<count; i++) {
            System.out.println((i+1)+" Disease name : " + DiseaseName);
            System.out.println(" Sympot1oms : " + Sympotoms);
            System.out.println(" Treatnent : " + Treatnent+"\n");
        }}
    }
    void SearchDisease(String dname){
        if(count==-1) {
            System.out.println(" file is empty");
        }else{
            for(int i=0; i<count; i++){
                if(DiseaseName==dname) {
                    System.out.println("is found");
                    System.out.println((i + 1) + " Disease name : " + DiseaseName);
                    System.out.println(" Sympot1oms : " + Sympotoms);
                    System.out.println(" Treatnent : " + Treatnent + "\n");
                }

        }

        }
    }
}
