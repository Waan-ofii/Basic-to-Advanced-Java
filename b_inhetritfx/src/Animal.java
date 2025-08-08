public class Animal {
    public void eat(){
        System.out.println("anumal is eating 1.... ");
    }
    public void walk() {
        System.out.println("Animal walking…");
    }
    public void speak() {
        System.out.println("Animal sound…");
    }
protected String name;
}

class Cat extends Animal {
    public  void chaseMouse() {
        System.out.println("Chasing mouse…2");
    }
}

class babycat extends Cat{
    void meow(){
        System.out.println("meow meow ");
    }
}
public class test {
    public static void main(String[] args) {
        Cat c=new Cat();
        c.walk();
        c.eat();
        c.speak();
        c.chaseMouse();
        c.name="tola";
        System.out.println(c.name);

        babycat b=new babycat();
        b.meow();
        b.eat();
        b.walk();
        b.chaseMouse();

    }
}