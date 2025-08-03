import java.util.Scanner;

public class TypeCasting {
    public static void main(String[] args) {
        //widening
        int x=7;
        long y=x;
        float z=x;
        double w=x;
        System.out.println("int : "+x);
        System.out.println("long : "+y);
        System.out.println("float : "+z);
        System.out.println("double : "+w+"\n");


        //narrowwing
        double d=11.12;
        float f=(float)d;
        int c=(int)f;

        System.out.println("double "+d);
        System.out.println("float "+f);
        System.out.println("int "+c);

        Scanner nn=new Scanner(System.in);
        int year;
        System.out.print("please enter a year : ");
        year=nn.nextInt();
        if(((year%4==0)&&((year%100)!=0))||(year%400==0)){
            System.out.println("is a leap year");
        }else{
            System.out.println("is not leap year ");
        }

    }
}
