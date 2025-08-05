package class_constractor;

public class J_constructor {
    int x;
    int a,b,h;
    J_constructor(){
        x=5;
    }
J_constructor(int z,int y){

        a=z;
        b=y;
        h=z+y;
}
    public static void main(String[] args) {
        J_constructor obj1 = new J_constructor();
        System.out.println(obj1.x);

        J_constructor obj2 = new J_constructor(10,20);
        System.out.println(obj2.h);
    }

}
