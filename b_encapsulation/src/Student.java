public class Student {

    private int id;
    private String name;
    private String lname;

    Student(int i, String n, String f) {
        this.id = i;
        this.name = n;
        this.setlname(f);
    }
    //to access we have to use getter methods

    public int getId() { return id;}

    public String getName() {
        return name;
    }

    public String getlname() {
        return lname;
    }

    //to change the value of variable use setter
    public void setlname(String f) {
        this.lname = f;
    }
}
