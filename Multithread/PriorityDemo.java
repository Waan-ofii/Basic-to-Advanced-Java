class PriorityDemo extends Thread {

    public PriorityDemo(String name) {
        super(name); // set thread name
    }

    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(
                Thread.currentThread().getName() +
                " (Priority: " + Thread.currentThread().getPriority() + 
                ") -> Count: " + i
            );
               System.out.println("-------------------------------");
               System.out.println("-------------------------------");
            try {
                Thread.sleep(10); // slow down output
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
            }
        }
    }

    public static void main(String[] args) {

        PriorityDemo t1 = new PriorityDemo("High Priority Thread");
        PriorityDemo t2 = new PriorityDemo("Low Priority Thread");
        PriorityDemo t3 = new PriorityDemo("Normal Priority Thread");

        // Set priorities
        t1.setPriority(Thread.MAX_PRIORITY);   // 10
        t2.setPriority(Thread.MIN_PRIORITY);   // 1
        t3.setPriority(Thread.NORM_PRIORITY);  // 5

        // Start threads
        t1.start();
        t2.start();
        t3.start();
    }
}