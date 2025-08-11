public class SleSuper {
    String color="white";
    SleSuper(){
        System.out.println("constructor from parent ");
    }
    void weyne(){
        System.out.println("weyne parent");
    }

}
class sdd extends SleSuper{
    String color="Black";
    sdd(){
        super();
        System.out.println("constructor from child");

    }
void weyne(){
    System.out.println("weyne child");
    super.weyne();
}
    void print(){
        System.out.println(color);
        System.out.println(super.color);

    }
}

class Dispaly{
    public static void main(String[] args) {
sdd hh=new sdd();

hh.print();
hh.weyne();

    }
}