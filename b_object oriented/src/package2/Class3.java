package package2;

import package1.Class1;
//to use protected one extends class1
public class Class3 extends Class1{
    public static void main(String[] args) {
        Class1 n = new Class1();
        //we can't access these two coz they are defualt
        //System.out.println(n.x);
      //  System.out.println(n.y);
//public can accessed in another package
        System.out.println(n.a);
        System.out.println(n.b);


//we can't access these two coz they are private
        // System.out.println(n.p);
        //System.out.println(n.q);

//we cannot access protected in other package of class
        //to acccess protected in other package of class use extended
        Class3 prt= new Class3();
        System.out.println(prt.g);
        System.out.println(prt.h);


    }
}
