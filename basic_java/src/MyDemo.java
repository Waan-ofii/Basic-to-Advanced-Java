// Interface Animal

interface Animal {
    void bark();
    void hello();
}
public class MyDemo implements Animal {
    @Override
    public void bark() {
        System.out.println("wof wof wof wtf");
    }

    @Override
    public void hello() {
        System.out.println("hey welcome back! ");
    }

    public static void main(String[] args) {
        MyDemo dog = new MyDemo();
        dog.bark();
        dog.hello();
    }
}
