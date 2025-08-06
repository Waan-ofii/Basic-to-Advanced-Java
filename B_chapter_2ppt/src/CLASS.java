import java.util.Scanner;

public class CLASS {
    public static void main(String[] args) {
        int siz=10;
        int count=1;

CLLASO[] p=new CLLASO[siz];
Scanner bb=new Scanner(System.in);
try {

    while (true)  {
        int choice;
        System.out.println("Student Registration system \n");
        System.out.println("press 1: To register ");
        System.out.println("press 2: To see All registered student ");

        System.out.print("\n please enter your choice : ");
        choice = bb.nextInt();
        switch (choice) {
            case 1:
                if(count<siz){
                    p[count]=new CLLASO();
                    p[count].register();
                }else{
                    System.out.println("sorry our school registered maximum Student");
                }
                count++;
                break;
            case 2:
                for (int i = 1; i < count; i++) {
                    p[i].Display();
                }
                break;
            default:
                System.out.println("invalid option ");
        }

    }
} catch (NullPointerException e) {
    System.out.println("something wrong"+e.getMessage());
}
    }
}

class CLLASO {
        private String name;
        private int age;
        private double gpa;


       public void register() {
           Scanner nn=new Scanner(System.in);
           System.out.println("please eneter studen name : ");
           name=nn.nextLine();
           System.out.println("please eneter studen age : ");
           age=nn.nextInt();
           System.out.println("please eneter studen gpa : ");
           gpa=nn.nextDouble();

       }

       public void Display(){
           System.out.println("name : "+name);
           System.out.println("age : "+age);
           System.out.println("gpa : "+gpa);
           System.out.println();

       }



}