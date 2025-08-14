public class Test {
    double[] salary = {100, 200, 300, 120, 230};

    public void Display() throws ArrayIndexOutOfBoundsException {
        for (int i = 0; i < 7; i++) {
            System.out.println(salary[i]);
        }
    }

    public static void main(String[] args) {
        try {
            Test nn = new Test();
            nn.Display();
        }catch(Exception xx){
            System.out.printf("exceptiom  "+xx.toString());
        }
    }
}
class Finnalyy{
    public static void main(String[] args) {
        try {
            int x = 300;
        }catch(Exception e){
            System.out.println(e);
        }finally {
            System.out.println("finall is  executed");
        }
    }
}