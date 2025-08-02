public class Staticblock {
    static {
        System.out.println("hello im static_block i don't need object to executed");
        System.out.println("executed only once at the time of class loading");
    }

    public static void main(String[] args) {
        System.out.println("this is from main  ");
    }
}