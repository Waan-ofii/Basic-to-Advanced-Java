public class Instanseblock {

       /* Instanseblock() {
            System.out.println("constructo makes executable instance acces specifier");
        }*/{
        System.out.println("this is instsanc block ");
        System.out.println("An instance block is executed in the order they are");
        System.out.println("written after the constructor makes the call to super");
    }
    public static void main(String[] args) {
        Instanseblock bb=new Instanseblock();

        System.out.println("hello instance im from main ");


    }
}
