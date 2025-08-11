public class multilevel {
    void methodp(){
        System.out.println("grand class");
    }
}
class middle extends multilevel{
    void methodM(){
        System.out.println("parent class");
    }
}
class child extends middle{
    void childM(){
        System.out.println("child class");
    }
    public static void main(String[] args) {
        child bb=new child();
        bb.methodp();
        bb.methodM();
        bb.childM();

    }
}