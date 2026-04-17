public class SleepMessage extends Thread{
    public void run(){
        String info[] = {"Mares eat oats", "Dogs eat oats", "Little lambs eat ivy", "A kid will eat ivy too"};
        for (int i = 0; i < info.length; i++) {
            try {
                Thread.sleep(4000); //sleep 4 seconds
                System.out.println(info[i]);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void main(String args[]) throws InterruptedException {
        SleepMessage sm = new SleepMessage();
        sm.start();
    }
}