import java.util.Scanner;

public class today {
    public static void main(String[] args) {

        System.out.println("hello java");
        int x, y, z;
        System.out.println("enter first number :");
        Scanner num = new Scanner(System.in);
        x = num.nextInt();
        System.out.println("enter second  number :");
        y = num.nextInt();
        System.out.println("enter third number :");
        z = num.nextInt();


        System.out.println("_______if else_____");

        if (x > y && x > z) {
            System.out.println(x + " is greatest number");
        } else if (y > x && y > z) {
            System.out.println(y + " is greatest number");
        }

       else{
            System.out.println(z + "  is greatest number");
        }

        System.out.println("_______for loop_____");
        for (int i = 0; i < x; i++) {
            System.out.println(i);
        }
        System.out.println("_______for each_____");
        int[] array = {10, 20, 30, 40, 50};


        for( int k:array) {
            System.out.println(k);
        }
        System.out.println("_______while loop_____");
        while(z>0){
            System.out.println(z);
            z--;
        }

        System.out.println("_______jump_____");
        int number=10;
        for(int a=0; a<number; a++) {
            if (a == 5) {
                break;
            }
            System.out.println(a);
        }

        System.out.println("_______triangle_____");
        for(int f=5;f>=1;f--){
            for(int g=1;g<=f;g++){
                System.out.print("*");
    }
            System.out.println();
        }


    }
}
