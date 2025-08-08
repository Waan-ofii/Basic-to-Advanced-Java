public class Bicyclee {

    public Bicyclee() {

        System.out.println("hello this is super class");
    }
    public void eat(){
        System.out.println("eateing");
    }

}
class Motorcycle extends Bicyclee{

    public void eat(){
        System.out.println("brEA DBRAD");}
    void bark(){
        System.out.println("barking");
    }
    void sub(){
        System.out.println("display all");
bark();
super.eat();
    }
}
class test{
    public static void main(String[] args) {
        Motorcycle mm=new Motorcycle();
       mm.sub();
    }
}