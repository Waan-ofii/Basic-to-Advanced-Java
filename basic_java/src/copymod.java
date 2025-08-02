 import java.util.Date;
 public class copymod{
        String id;
        String name;
        Date birthdate;
        public copymod(String i, String n/* Date b*/){
            id = i;
            name = n;
            //birthdate = b;
        }
        public copymod(copymod s){
            id = s.id;
            name =s.name;
          //  birthdate = s.birthdate;
        }
        void display() {
            System.out.println("ID: " + id);
            System.out.println("Name: " + name);
           // System.out.println("Birthdate: " + birthdate);
        }
        public static void main(String args[]) {
            copymod s1 = new copymod("ru0101", "Karan");
            copymod s2 = new copymod(s1);
        s1.display();
        s2.display();
        }
    }

