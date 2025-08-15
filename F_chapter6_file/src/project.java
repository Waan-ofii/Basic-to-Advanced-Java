import java.io.*;
import java.util.*;

public class project implements Serializable {
    private String name;
    private int age;
    private double gpa;

    void register() {
        Scanner nn = new Scanner(System.in);

        System.out.println("Please enter student name: ");
        name = nn.nextLine();
        System.out.println("Please enter student age: ");
        age = nn.nextInt();
        System.out.println("Please enter student GPA: ");
        gpa = nn.nextDouble();

        System.out.println("STUDENT REGISTERED SUCCESSFULLY!!!!!!!!!!");
    }

    public void display() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        System.out.println("GPA: " + gpa);
        System.out.println();
    }
}

class Barata {
    public static void main(String[] args) {
        List<project> students = new ArrayList<>();
        Scanner vv = new Scanner(System.in);
        int choice;
        boolean laal = true;

        while (laal) {
            System.out.println("Student Registration System");
            System.out.println("Press 1: To register");
            System.out.println("Press 2: To see registered students");
            System.out.println("Press 3: To Exit");
            System.out.print("\nPlease enter your choice: ");
            choice = vv.nextInt();

            switch (choice) {
                case 1:
                    project student = new project();
                    student.register();
                    students.add(student); // Add student to list
                    saveStudents(students); // Save to file after registration
                    break;

                case 2:
                    List<project> loadedStudents = loadStudents(); // Load students from file
                    if (loadedStudents.isEmpty()) {
                        System.out.println("No students registered yet!");
                    } else {
                        for (project p : loadedStudents) {
                            p.display();
                        }
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

    // Method to save students to a file
    private static void saveStudents(List<project> students) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("students.ser"))) {
            out.writeObject(students);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to load students from a file
    private static List<project> loadStudents() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("students.ser"))) {
            return (List<project>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>(); // Return empty list if no students are found
        }
    }
}
