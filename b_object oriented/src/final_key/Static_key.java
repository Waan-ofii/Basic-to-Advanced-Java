package final_key;

public class Static_key {
    int id;
    String name;
    static String dept="cs";

    Static_key(int i, String n){
       id=i;
       name=n;

    }
    void display(){
        System.out.println("id= "+id+" name= "+name+" dept= "+dept);
    }
   static void change(){
        dept="maths";
    }

    public static void main(String[] args) {


        Static_key s1=new Static_key(22,"Bona");
        Static_key s2=new Static_key(33,"Chala");
       //dept are displayed for all object coz it is static key

        change();

        s1.display();
        s2.display();
    }
}
