package method_with_paramemeter_and_return;

public class Meth_parameter {
    static void add(int a) {
        System.out.println(a+10);
    }

    static int sum(int c, int d){
        return c+d;
    }

    public static void main(String[] args) {
        System.out.println("example for parameter");
        add(5);
        System.out.println("example for return value");
        System.out.println(sum(2,4));
    }
}
