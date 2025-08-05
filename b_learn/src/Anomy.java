abstract class Animal{
   abstract void makesound();
}



public class Anomy {
    public static void main(String[] args) {
        Animal dog = new Animal() {
            @Override
            void makesound() {
                System.out.println("wof wof wof ");
            }
        };
dog.makesound();
    }
}