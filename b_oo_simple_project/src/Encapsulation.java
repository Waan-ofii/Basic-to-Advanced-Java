public class Encapsulation {
    private String name;
    private String id;
    private int age ;
    private String department;

    public String getname(){
        return name;
    }
    public void Setname(String nam){
        this.name=nam;
    }

    public String getId(){
        return id;
    }
public void Setid(String ID){
    this.id= ID;
}
public int getsge(){
        return age;
}
public void setage(int ag){
        this.age=ag;
}
public String getdepartment(){
        return department;
}

public void setdepartment(String dept){
        this.department=dept;
}

    public static void main(String[] args) {
        Encapsulation bb=new Encapsulation();
        bb.Setname("tola");
        bb.Setid("ru3322/23");
        bb.setdepartment("sw");
        bb.setage(24);


        System.out.println(bb.getname());
        System.out.println(bb.getId());
        System.out.println(bb.getsge());
        System.out.println(bb.getdepartment());
    }
}
