public class lab_exception {

    public static void main(String[] args) {
        try {

            int b[]={1,2,3,4,5};
            System.out.println(b[8]);
            int a = 50 / 0;
            System.out.println(a);
        }
        catch(ArithmeticException a ){
            System.out.println(a.toString());
        }
        catch(ArrayIndexOutOfBoundsException b) {
           System.out.println("jazba essa si fidu wanta hin jirree");

       }
    }
}
