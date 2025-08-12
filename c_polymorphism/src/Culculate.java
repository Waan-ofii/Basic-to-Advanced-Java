public class Culculate {
    public void compute(int a, int b){
        System.out.println(a+ " and "+b);
    }
}

class Add{
    public void compute(int a, int b){
        System.out.println(a+b);
}
}

class Sub{
    public void compute(int a, int b){
        System.out.println(a-b);
    }
}
class Mult{
    public void compute(int a, int b){
        System.out.println(a*b);
    }
}

class Poly{
    public static void main(String[] args) {
        Culculate c=new Culculate();-
        Add a=new Add();
        Sub s=new Sub();
        Mult m=new Mult();

    c.compute(8,4);
    a .compute(8,4);
    s.compute(8,4);
    m.compute(8,4);


    }
}