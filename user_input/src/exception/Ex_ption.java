package exception;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Ex_ption {
    public static void main(String[] args) {
        try {
            Scanner num = new Scanner(System.in);
            System.out.println("enter first number : ");
            int n1 = num.nextInt();

            System.out.println("enter second number : ");
            int n2 = num.nextInt();

            System.out.println(n1 / n2);
        } catch (ArithmeticException e) {

            System.out.println("any number cannot devide by zero!");
        }

       catch (InputMismatchException e) {

           System.out.println("please enter only number !");

        }

    }
}
