public class Animo {
    void show(){
        System.out.println("hi there");
    }
}
class dog extends Animo{
    public void dogy(){
        System.out.println("dog is animal");
    }
}
class babydog extends dog{
    public void bbdd(){
        System.out.println("hey baby dog");
    }
}
class babBrodog extends dog{
    public void bbbro(){
        System.out.println("hey baby brada dog");
    }
}
class Test{
    public static void main(String[] args) {
        babydog bb=new babydog();
        bb.bbdd();
        bb.dogy();
        bb.show();
        babBrodog bg=new babBrodog();
        bg.bbbro();
        bg.dogy();
        bg.show();


    }
}