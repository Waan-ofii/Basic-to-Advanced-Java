class couter2{

    String name="outer one";
  void ask(){
      System.out.println("hello static nested class can you acces data from outer class");
  }

    static class inner2{
        String name2="inner one";

        void print(){
            System.out.println("static inner class");
            System.out.println("no i cannot im static ");
        }

    }
}




public class Nest {
    public static void main(String[] args) {
        couter2 b = new couter2();
        b.ask();
        System.out.println(b.name);
  couter2.inner2 bb=new couter2.inner2();

        System.out.println(bb.name2);

bb.print();
    }
}
