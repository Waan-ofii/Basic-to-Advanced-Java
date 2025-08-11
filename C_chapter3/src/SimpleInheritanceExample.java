public class SimpleInheritanceExample  {
    public static void main(String[] args) {
        cat bb=new cat();
        bb.eating();
        bb.talk();
        bb.walk();
        bb.aname="anime";//defoult  variable can be inherited
       // bb.age=33; cannot accessible coz its private
        bb.number=66;//protected also accessible
    }

}
 class Animal{
    String aname;
  private  int age;
  protected int number;

    void walk(){
        System.out.println("animal is walking ");
    }
    void talk(){
        System.out.println("animal is talking");
    }
}
class cat extends Animal{
    void eating(){
        System.out.println("cat is chasing rat");
    }
}