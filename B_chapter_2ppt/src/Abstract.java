public abstract class Abstract {
void nonabs(){
    System.out.println("this not abstract method");
}
    abstract void method();
}


class son extends Abstract{
    void method(){
        System.out.println("abstract method ");
    }

    public static void main(String[] args) {
        son obj=new son();
        obj.method();
        obj.nonabs();
    }
}