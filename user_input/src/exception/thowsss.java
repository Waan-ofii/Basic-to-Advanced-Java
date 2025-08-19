package exception;

import javax.lang.model.type.NullType;

public class thowsss {
    public static void checkage(int age) throws ArithmeticException,ArrayIndexOutOfBoundsException{
        if (age < 18) {
            //throw new ArithmeticException("age must be 18+ to vote ");
            System.out.println("age must be 18+ to vote");
        }else{
            System.out.println("Eligible to vote");
        }
}
    public static void go() throws NullPointerException {
        String s = null;
        int b = s.length();
        System.out.println(b);
    }

    public static void main(String[] args) {

        try {
            checkage(16);
go();
        } catch (Exception a) {
            System.out.println(a.getLocalizedMessage());

        }
    }}