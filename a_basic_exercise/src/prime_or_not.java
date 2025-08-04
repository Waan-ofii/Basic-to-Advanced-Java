import java.util.Scanner;

public class prime_or_not {
    public static void main(String[] args) {
        int counter=0;
        Scanner scan = new Scanner(System.in);
        System.out.println("please enter a number : ");
        int num = scan.nextInt();


        if (num > 1) {

            for (int i = 1; i <= num; i++) {
                if (num % i == 0) {
                    counter++;
                }
            }

            if (counter == 2) {
                System.out.println(num + " is prime number ");

            } else {
                System.out.println(num + " is not prime number ");
            }
        }
        }
    }