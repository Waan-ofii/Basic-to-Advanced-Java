public class Main {
   public static void main(String[] args) {
    Thread th = Thread.currentThread();
    System.out.println("current thread whole name : " + th);
    System.out.println("current thread name : " + th.getName());
    System.out.println("priority : "+ th.getPriority());
    //change Thread name
    th.setName("Mythread");
    System.out.println("after name changed : "+ th);
    System.out.println("name : "+ th.getName());
    System.out.println("priority : "+ th.getPriority());

    try{
for(int n=5; n>0; n--){
    System.out.println(n);
    Thread.sleep(1000);
}
    }catch(InterruptedException e){
        System.out.println("Main thread interrupted");
    }
   } 
}
