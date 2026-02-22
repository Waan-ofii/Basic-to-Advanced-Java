public class ThreadPriority {
public static void main(String args[]) {
Thread tt1 = new OneThread("1");
Thread tt2 = new OneThread("2");      
tt1.setPriority(8);
tt2.setPriority(2);
tt1.start();
tt2.start();
}
}
class OneThread extends Thread {
String name;
OneThread(String nm) {
name = nm;
}
public void run() {
for (int i = 1; i <= 50; i++) {
System.out.println("Child Thread" + name + ": " + i);
}
System.out.println("Exiting child thread " + name + ".");
}
}
