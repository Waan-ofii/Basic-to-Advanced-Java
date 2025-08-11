//abstract class
public abstract class Person {
    private String name;
    private String gender;

    public Person(String nm, String gen) {
        this.name = nm;
        this.gender = gen;
    }

    //abstract method
    public abstract void work();

    @Override
    public String toString() {
        return "Name=" + this.name + "::Gender=" + this.gender;
    }

    public void changeName(String newName) {
        this.name = newName;
    }
}
class tosee extends Person{

    public tosee(String nm, String gen) {
        super(nm, gen);
    }

    public void work() {

        System.out.println("hello");
    }
    public static void main(String[] args) {
        tosee dd=new tosee("ss","s");
        dd.work();
        System.out.println(dd.toString());
    }
}