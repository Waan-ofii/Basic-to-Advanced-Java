     class Counter {
    private int c = 0;
    public  synchronized void increment() {
        c++;
    }
    public synchronized void decrement() {
        c--;
    }
    public synchronized int value() {
        return c;
    }
}
class CounterThread extends Thread {
    static Counter sc = new Counter();
    String name;
    public CounterThread(String nn) {
        name = nn;
    }  
    public void run() {
        for(int i = 0; i < 10; i++) {
            sc.increment();
            System.out.println("Thread " + name + ": " + sc.value());
        }
    for(int i = 0; i < 5; i++) {
            sc.decrement();
            System.out.println("Thread " + name + ": " + sc.value());
        }
    }
}
class SyncTest {
    public static void main(String args[]) {
        CounterThread ct1 = new CounterThread("1");
        CounterThread ct2 = new CounterThread("2");
        ct1.start();
        ct2.start();
    }
}