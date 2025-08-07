//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
Student s1=new Student(123,"Tesfu","AT");
//s1.name; we can't initilize like this coz it's hiden

        s1.setlname("Ayele");
        System.out.println(s1.getId());
        System.out.println(s1.getName());
        System.out.println(s1.getlname());
    }
}