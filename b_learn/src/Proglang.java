interface Do{
    void name();
    void whodevelop();
}


class java implements Do {
    @Override
    public void name() {
        System.out.println("oop java");
    }

    @Override
    public void whodevelop() {
        System.out.println("James Gosling");
    }
}
    class cpp implements Do {
        @Override
        public void name() {
            System.out.println("computer programming language");
        }

        @Override
        public void whodevelop() {
            System.out.println("Bjarne Stroustrup");
        }

    }
        public class Proglang {
            public static void main(String[] args) {

           java j=new java();
           j.name();
           j.whodevelop();
           cpp c=new cpp();
           c.name();
           c.whodevelop();

            }
        }


