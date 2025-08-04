import java.util.Scanner;

public class swamping {
    public static void main(String[] args) {
        Scanner num=new Scanner(System.in);
        System.out.print("please enter n1= ");
        int n1= num.nextInt();
        System.out.print("please enter n2= ");
        int n2= num.nextInt();
        System.out.println("Before swamping   n1= "+n1+" n2= "+n2);

        //swamp using third variable temp
        int temp;
        temp=n1;
        n1=n2;
        n2=temp;

        System.out.println("After swamping   n1= "+n1+" n2= "+n2);




        System.out.print("please enter A= ");
        int A= num.nextInt();
        System.out.print("please enter B= ");
        int B= num.nextInt();
        System.out.println("Before swamping   A= "+A+" B= "+B);

        //swamp without using third variable temp by addition and substraction
        A=A+B;
        B=A-B;
        A=A-B;

        System.out.println("After swamping   A= "+A+" B= "+B);


    }

}
