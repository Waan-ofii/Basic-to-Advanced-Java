public class hashcode {
    private String name;
    private int age;

hashcode(String name,int age){
    this.name=name;
this.age=age;
}   String getname(){
        return name;
    }

void setName(String nam){
this .name=nam;
}

    int getage(){
        return age;
    }

    void setage(int ag){
        this .age=ag;
    }

    public static void main(String[] args) {
        hashcode b1=new hashcode("chala",22);
        hashcode b2=new hashcode("chala",22);

        int w=b1.hashCode();
        int x=b2.hashCode();
        System.out.println(" b1 :  "+w);
        System.out.println(" b2 :  "+x);
        System.out.println("is the object equal : "+b1.equals(b2));
}
}


