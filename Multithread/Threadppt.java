public class Threadppt {
    public static void main(String[] args) {
        Thread t=Thread.currentThread();
        System.out.println("Current thread :" + t);
        System.out.println("--------------------------");
        t.setName("Tola Thread");
        System.out.println("after name changed :"+t);
        try {
            for(int n=5; n>0; n--){
                System.out.println(n);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            System.out.println("something is wrong");
        }
    }
}
