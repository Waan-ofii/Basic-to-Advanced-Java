package another;

public class ex_ption {
    public static void main(String[] args) {
        try {
            int b = 30 / 0;
            System.out.println(b);
            String s = null;
            System.out.println(s.length());
        }
        catch (ArithmeticException | NullPointerException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
