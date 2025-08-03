import java.util.Scanner;

public class Quadratic {
    public static void main(String[] args) {
        double a,b,c;
        Scanner q=new Scanner(System.in);
        System.out.println("please enter quadratic entities");
        System.out.print("enter values of a = ");
        a=q.nextDouble();
        System.out.print("enter values of b = ");
        b=q.nextDouble();
        System.out.print("enter values of c = ");
        c=q.nextDouble();
        double inner=Math.pow(b,2)-(4*a*c);
        double Sqt=Math.sqrt(inner);

        if(inner>0){
            double r1=((-b)+Sqt)/2*a;
            double r2=((-b)-Sqt)/2*a;
            System.out.println("root r1 = "+r1);
            System.out.println("root r2 = "+r2);
        }else if(inner<0){
            System.out.println("root is imaginary number ");
        }else{
            double r=-b/2*a;
            System.out.println("root  r = "+r);
        }

    }
}
