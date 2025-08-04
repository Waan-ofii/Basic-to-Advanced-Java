import java.util.Scanner;

public class larg {
    public static void main(String[] args) {
        Scanner num=new Scanner(System.in);
        System.out.println("please enter first number :");
        int n1= num.nextInt();
        System.out.println("please enter second number :");
        int n2= num.nextInt();
        System.out.println("please enter third number :");
        int n3= num.nextInt();

                if(n1>n2&&n1>n3){
                    System.out.println(n1+" is largest number");
                }
                else if(n2>n1&&n2>n3){
                    System.out.println(n2+" is largest number");
                }
                else {
                    System.out.println(n3+" is largest number ");
                }
    }
}
