public class Interrupts extends Thread{

    public void run() {
String info[] = {"Mares eat oats", "Dogs eat oats", "Little lambs eat ivy", "A kid will eat ivy too"};
  for (int i = 0; i < info.length; i++) {
    try {
        Thread.sleep(4000);
    } catch (InterruptedException e) {
        //thread is interrupted: no more messages.
        return;
    }
    System.out.println(info[i]);   
  }
}
    public static void main(String[] args) {
        Interrupts tt=new Interrupts();
        tt.start();
        
    }
}
