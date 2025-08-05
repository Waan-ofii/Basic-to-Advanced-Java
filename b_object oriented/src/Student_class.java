public class Student_class {
    int id;
    String name;

    void register(int i, String n){
        id=i;
        name=n;
    }

    void disolay(){
        System.out.println(id + " " + name);
    }

    public static void main(String[] args) {
        //creating object
        Student_class s1= new Student_class();
        Student_class s2= new Student_class();


        s1.register(001,"Nati");
        s2.register(002,"Mekdi");

        s1.disolay();
        s2.disolay();
    }
}
