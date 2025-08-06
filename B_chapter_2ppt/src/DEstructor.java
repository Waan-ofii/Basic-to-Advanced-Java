public class DEstructor {

    public void finalize(){

        System.out.println("object is destroyed");
    }
    public static void main(String[] args) {
        DEstructor nn=new DEstructor();


        nn=null;
        System.gc();
    }


}
