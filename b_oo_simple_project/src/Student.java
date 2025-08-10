public class Student {
    protected String name;
    protected String id;
    protected int phone;

    Student(){
        name="tola";
        id="tu0334";
        phone=5563744;
    }
    Student(String nam,String Id,int ph){
        name=nam;
        id=Id;
        phone=ph;
    }
    public void setname(String nm){
        name=nm;
    }
    public void setid(String ID){
        id=ID;
    }
    public void setPhone(int phh){
        phone=phh;
    }
    public String getname(){
        return name;
    }
    public String getid(){
        return id;
    }
    public int getphone(){
        return phone;
    }

    public void Display(){
        System.out.println(" name : "+name+"\n ID : "+id+"\n pnone :"+phone );
    }
}
class sw extends Student{
    private String rollNumb;
    private int mark;

    sw(){
        super();
        rollNumb="44";
        mark=56;
    }
    sw(String a,String b,int c,String d,int e){
        super(a,b,c);
        rollNumb=d;
        mark=e;
    }

    public void setRollNumb(String rn){
        rollNumb=rn;
    }
    public void setMark(int mr){
        mark=mr;
    }
    public String getRollNumb(){
        return rollNumb;
    }
    public int getMark(){
        return mark;
    }

    void display(){
        super.Display();
        System.out.println(" rollNumber : "+rollNumb+"\n mark : "+mark);
    }
}
class Runnner{
    public static void main(String[] args) {
        sw s=new sw();
        s.display();
    }
}