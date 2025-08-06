public class Encabsulation {
    private String name;
    private int age;

    String getname(){
        return name;
    }
    void setname(String nam){
        this.name=nam;
    }
    int getage(){
        return age;
    }

    void setage(int ag){
        if(ag>18){
            this.age=ag;
        }else{
            System.out.println("you are not eleigible to  vote");
        }
    }

    public static void main(String[] args) {
        Encabsulation kk=new Encabsulation();
        kk.setname("tola");
        kk.setage(25);
        System.out.println("name "+kk.getname());
        System.out.println("age "+kk.getage());
    }
}
