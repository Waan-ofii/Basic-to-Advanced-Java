class Isa_alaa{
    void Methods(){
        class Local_cls{
            void display(){
                System.out.println("this is from local class ");
            }
        }

        Local_cls in=new Local_cls();
        in.display();
    }
}



public class loci {
    public static void main(String[] args) {
        Isa_alaa n=new Isa_alaa();
        n.Methods();
    }
}
