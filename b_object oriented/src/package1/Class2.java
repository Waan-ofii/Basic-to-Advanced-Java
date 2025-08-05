package package1;

public class Class2 {
    public static void main(String[] args) {
        Class1 n = new Class1();

        //private can accessed in another class
        System.out.println(n.x);
        System.out.println(n.y);

//public can accessed in another class
        System.out.println(n.a);
        System.out.println(n.b);


//we can't access these two coz they are private
       // System.out.println(n.p);
        // System.out.println(n.q);

//protected can accesed in subclasses in the same package
        System.out.println(n.g);
        System.out.println(n.h);


    }
}