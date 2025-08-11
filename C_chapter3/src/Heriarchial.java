public class Heriarchial {
    void Animal(){
        System.out.println("animal eats ");
    }

}
class Cat extends Heriarchial{
    void cats(){
        System.out.println("meow");
    }

    public static void main(String[] args) {
        Cat mm=new Cat();
        mm.Animal();
        mm.cats();
    }
}
class dog extends Heriarchial{
    void dogs(){
        System.out.println("wuwu wuwu");

    }

    public static void main(String[] args) {
        dog ww=new dog();
        ww.Animal();
        ww.dogs();
    }
}