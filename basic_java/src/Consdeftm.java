public class Consdeftm {
    public double width, height, area, perimeter;


    public Consdeftm(double w, double h) {
        width = w;
        height = h;
    }

    public static void main(String args[]) {
        Consdeftm recto = new Consdeftm(3,4); //error
        System.out.println(recto.width);

    }
}