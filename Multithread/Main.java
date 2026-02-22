
 class multithread extends Thread {    public void run(){
        for(int i=0; i<10; i++){
System.out.println(i);
        }
        System.out.println(Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getPriority());
    }
}
class multithread2 extends Thread{
    public void run(){
        int j=5;
        while(j>0){
System.out.println(j);
j--;
        }
         System.out.println(Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getPriority());
    }
}
class Main{
    public static void main(String[] args){
        System.out.println(Thread.currentThread().getName());
multithread m1=new multithread();
multithread2 m2= new multithread2();
m1.start();
m2.start();
    }

}