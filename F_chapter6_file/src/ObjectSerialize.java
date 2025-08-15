/*import java.io.*;


class student implements Serializable{
    String name;
    int age;
}


public class ObjectSerialize {
    public static void main(String[] args) {
        student s=new student();
        s.name="John";
        s.age=34;
try {
FileOutputStream filex=new FileOutputStream("student.ser");
    ObjectInputStream out = new ObjectInputStream(filex);

    out.writeObject(s);
    out.close();
    filex.close();
} catch (IOException e) {
    e.printStackTrace();
}
    }

}*/
import java.io.*;
class Student implements Serializable {
    String name;
    int numbr;
    double tax;

    Student(String nam, int nm, double tx) {
        this.name = nam;
        this.numbr=nm;
        this.tax=tx;
    }
}
public class ObjectSerialize {
    public static void main(String[] args) {
        Student s = new Student("Borif", 101, 3.85);

        // Serialize the student object to a file
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("student.ser"))) {
            out.writeObject(s);
            System.out.println("Student object serialized to student.ser");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Deserialize the student object from the file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("student.ser"))) {
            Student  deserializedStudent= (Student) in.readObject();
            System.out.println("\nDeserialized Student object:");
            System.out.println(s.name);
            System.out.println(s.numbr);
            System.out.println(s.tax);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
