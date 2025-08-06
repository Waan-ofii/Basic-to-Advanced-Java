public class snested {
    void outerm(){
        System.out.println("tjhis is outer m");
    }
    void common(){
        System.out.println("this is common from outer");
    }

    static class innerStaticclass{

        void commom(){
            System.out.println("this is common from inner ");
        }
        void  innermethod(){
            System.out.println("this is from inner one ");
            this.commom();
           // snested.this.common(); static innner class cannot call like this coz it does not access other inclosing
        }
    }

    public static void main(String[] args) {
       snested.innerStaticclass innobj=new  snested.innerStaticclass();
       innobj.innermethod();
    }

}
