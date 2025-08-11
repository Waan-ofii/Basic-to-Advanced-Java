public class Subcontr {
    String name;
    Subcontr(int rrr){
        System.out.println("frist for  constructor from parent!!!");
        System.out.println("kk "+rrr);
    }

}
class sub extends Subcontr{
    sub(){
        super(5);
        System.out.println("jjj");
    }

    public static void main(String[] args) {
        sub bb=new sub();

    }
}