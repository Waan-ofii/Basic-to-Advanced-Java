import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        plant p = new plant();

        int op;
        do {
            System.out.println(" PLANT  DISEASE MANAGEMENT SYSTEM \n");
            System.out.println("press 1: To Add Disease ");
            System.out.println("press 2: To Display Disease ");
            System.out.println("press 3: To Search by Disease name\n ");
            System.out.print("\n please enter your choice ");
            Scanner inter=new Scanner(System.in);
            op= inter.nextInt();

            switch(op){
                case 1:
                    p.AddDisease();
                    break;
                case 2:
                    p.dispalyDisease();
                    break;
                case 3:
                    String dname;
                    System.out.print("please enter disease name you search for : ");
                    dname=inter.nextLine();
                    p.SearchDisease(dname);
                    break;
                default:
                    System.out.println("invalid option ");
            }
        } while (true);
    }
}