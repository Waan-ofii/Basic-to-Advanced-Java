class ThreadError extends Thread {
    static int count = 0;
    public void run() {
        for (int x = 0; x < 10; x++) {
            count++;
            count--;
        }
        System.out.println(this.getName() + " count: " + count);
    }
    public static void main(String[] args) throws InterruptedException {
        ThreadError t1 = new ThreadError();
        ThreadError t2 = new ThreadError();
        t1.start();
        t2.start();
    }
}