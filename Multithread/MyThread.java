class MyThread extends Thread {

    public MyThread(String name) {
        setName(name); // setName()
    }

    public void run() {
        System.out.println(getName() + " started with priority " + getPriority());

        for (int i = 1; i <= 3; i++) {
            System.out.println(getName() + " running: " + i);

            try {
                Thread.sleep(500); // sleep()
            } catch (InterruptedException e) {
                System.out.println(getName() + " was interrupted!");
                return;
            }

            Thread.yield(); // yield()
        }
    }

    public static void main(String[] args) {

        MyThread t1 = new MyThread("Thread-1");
        MyThread t2 = new MyThread("Thread-2");

        t1.setPriority(8); // setPriority()
        t2.setPriority(3);

        t1.start(); // start()
        t2.start();

        // activeCount()
        System.out.println("Active threads: " + Thread.activeCount());

        // enumerate()
        Thread[] arr = new Thread[Thread.activeCount()];
        int n = Thread.enumerate(arr);

        for (int i = 0; i < n; i++) {
            System.out.println("Thread found: " + arr[i].getName());
        }

        // interrupt example
        t2.interrupt();
    }
}