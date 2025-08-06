public interface myInterfac {
   public void show();

    public void talk();
}
class Myclass implements myInterfac{
    public void show(){
        System.out.println("let me show you what is going on");
    }
    public void talk(){
        System.out.println("give me your ear i wanna talk to to you");
    }

    public static void main(String[] args) {
        Myclass obj1=new Myclass();
        obj1.show();
        obj1.talk();
    }

}

