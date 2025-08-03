import java.io.*;
import java.util.Scanner;

// Make the class serializable
public class project implements Serializable {
    private String name;
    private int age;
    private double gpa;

    // Register method to input and serialize the object
    void Register() {
        Scanner nn = new Scanner(System.in);

        System.out.println("please enter student name: ");
        name = nn.nextLine();
        System.out.println("please enter student age: ");
        age = nn.nextInt();
        System.out.println("please enter student gpa: ");
        gpa = nn.nextDouble();

        try (ObjectOutputStream galchi = new ObjectOutputStream(new FileOutputStream("student.ser"))) {
            galchi.writeObject(this); // serialize the current object
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("STUDENT REGISTERED SUCCESSFULLY!!!!!!!!!!");
    }

    public void Display() {
        System.out.println("name : " + name);
        System.out.println("age : " + age);
        System.out.println("gpa : " + gpa);
        System.out.println();
    }
}

class barata {
    public static void main(String[] args) {
        int size = 10;
        project[] bb = new project[size]; // array of student objects
        Scanner vv = new Scanner(System.in);
        int count = 0;

        int choice;
        boolean laal = true;

        while (laal) {
            System.out.println("Student Registration System");
            System.out.println("press 1: to register");
            System.out.println("press 2: to see registered student ");
            System.out.println("press 3: to Exit ");
            System.out.print("\nplease enter your choice: ");
            choice = vv.nextInt();

            switch (choice) {
                case 1:
                    if (count < size) {
                        bb[count] = new project();
                        bb[count].Register();
                        count++;
                    } else {
                        System.out.println("Sorry, we can't take any more students.");
                    }
                    break;

                case 2:
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("student.ser"))) {
                        project p = (project) in.readObject(); // read a single student object

                        p.Display(); // display that one student

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case 3:
                    laal = false;
                    break;

                default:
                    System.out.println("Invalid Option");
            }
        }
    }
}

