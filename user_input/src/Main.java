import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

    Scanner input=new Scanner(System.in);
       // System.out.println("What is your name? ");
       //String name= input.next();//to input single word without space: next
      // System.out.println("your name is  :"+name);

        System.out.println("hey tell me  your full name ? ");
        String fullname= input.nextLine();//to insert more than one word with space :nextLine
        System.out.println("ok sir my name is "+fullname);


        System.out.println("how old are you : ");
        int age= input.nextInt();
        System.out.println("I am "+age+" years old");
        }
    }
