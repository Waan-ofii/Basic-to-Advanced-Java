public class PriorityDemo {
    public static void main(String[] args) {
        // Create multiple threads
        Thread lowPriorityThread = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("Low Priority Thread - Count: " + i);
            }
        });
        
        Thread highPriorityThread = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                System.out.println("High Priority Thread - Count: " + i);
            }
        });
        
        // Set priorities
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);   // Priority 1
        highPriorityThread.setPriority(Thread.MAX_PRIORITY);  // Priority 10
        
        // Start threads (high priority usually runs more often)
        lowPriorityThread.start();
        highPriorityThread.start();
    }
}