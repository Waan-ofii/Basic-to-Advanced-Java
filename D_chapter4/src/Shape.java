public class Shape {
    double d1;
    double d2;


    Shape(double dd1, double dd2){
        d1=dd1;
        d2=dd2;
    }

    double Area(){
        System.out.print("Area undefined ");
        return 0;
    }
}
class Triangle extends Shape{
    Triangle(double a,double b){
        super(a,b);
    }

    double Area(){
        System.out.print("triangle : ");
        return (d1*d2)/2;
    }
}
class Rectangle extends Shape{
    Rectangle(double a,double b){
        super(a,b);
    }
    double Area(){
        System.out.print("rectangle : ");
        return (d1*d2);
    }
}

class Dispaly {
    public static void main(String[] args) {
        Shape s = new Shape(4, 5);
        System.out.println(s.Area());
        s=new Rectangle(4,3);
        System.out.println(s.Area());
        s=new Triangle(4,5);
        System.out.println(s.Area());


    }
}