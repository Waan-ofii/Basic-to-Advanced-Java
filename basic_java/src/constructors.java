
public class constructors {
    public double width, height, area, perimeter;
    public constructors(double w, double h) {
        width = w;
        height =h;//The default constructor is not created if you declare any constructors for the class.

    }
    public static void main(String args[]) {
        constructors recto = new constructors(4,5); //error
        System.out.println(recto.width);
}
}
