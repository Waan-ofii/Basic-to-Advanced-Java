class outer {
    String name = "outer_class";
    int num = 12;

    public void outer_method() {
        System.out.println("method for outer one");
    }


    class inner {
        void common(){
            System.out.println("common for inner");
        }
        void display() {
            System.out.println("tell me the inner function");
            System.out.println("i can acces data from outer class, num= " + num);
            System.out.println("thos is non-static nested class ");
            outer.this.outer_method();//caling outer class method
           this.common();//callin inner class method
        }
    }
}


    public class Main {
        public static void main(String[] args) {
            outer n = new outer();
            outer.inner nn = n.new inner();
            System.out.println(n.name);
            nn.display();
        }
    }
