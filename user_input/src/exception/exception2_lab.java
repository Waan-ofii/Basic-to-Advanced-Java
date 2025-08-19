package exception;

public class exception2_lab {
    public static void checkage(int age) {
        if (age < 18) {
            throw new ArithmeticException("age must be 18+ to vote ");
        }else{
            System.out.println("Eligible to vote");
        }
    }
    public static void go()  {
        throw new ArrayIndexOutOfBoundsException("arry out of bound");
        String s = null;
        int b = s.length();
        System.out.println(b);

    }
    public static void main(String[] args) {
        try{
           checkage(16);
go();
        }
        catch (Exception a){
            System.out.println(a.getLocalizedMessage());

        }
    }
}
