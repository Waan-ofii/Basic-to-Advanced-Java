class inform{

    inform(){
        System.out.println("helloo finalizer");
    }

  @Override
    public void finalize() throws Throwable {
        System.out.println("already destroyed");
    }
}




public class finalizedd {
    public static void main(String[] args) {
        inform g=new inform();


        g=null; //eligable for destroyed
        System.gc();
    }
}
