public class copycons {
    String name;
    int age;

    public copycons(String n, int a){
        name =n;
        age=a;
    }
    public copycons(copycons g){
        name =g.name;
        age=g.age;
    }
    void display(){
        System.out.println("name :"+name+" age :"+ age);
    }
    public static void main(String[] args) {
        copycons st1=new copycons("Bona ",22);
        copycons st2=new copycons(st1);
        st1.display();
        st2.display();

    }
}
