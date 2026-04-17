public class Counter extends Thread {
    private int c = 10;
    public void increment() {
        c++;
        System.out.println(c);
    }
    public void decrement() {
        
        System.out.println(c);
    }

    public static void main(String[] args) {
        Counter d=new Counter();
        
d.increment();
Counter b=new Counter();
  b.decrement();


    }
}
