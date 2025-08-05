package package1;

public class Class1 {
    //default modifier
    int x=5;
    int y=7;

    //public modifier
    public int a=10;
    public int  b=20;

    //private modifier
    private int p=123;
    private int q=345;

    //protected mdifiers
   protected int g=1000;
   protected int h=4000;

    public static void main(String[] args) {
     Class1 prv=new Class1();
        System.out.println(prv.p);
        System.out.println(prv.q);
    }
}
