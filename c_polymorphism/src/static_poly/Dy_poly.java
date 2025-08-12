package static_poly;

class Dy_poly {
    //dynamic polymorphism use overridind
    public void operate(int a, int b) {
        System.out.println("a= " + a + " b= " + b);
    }
}

class add {
    public void operate(int a, int b) {
        System.out.println(a + b);
    }
}

class sub {
    public void operate(int a, int b) {
        System.out.println( a - b);
    }
}

class mult {
    public void operate(int a, int b) {
        System.out.println( a*b);
    }
}

 class See{
    public static void main(String[] args) {
        Dy_poly d=new Dy_poly();
        add a=new add();
        sub s=new sub();
        mult m=new mult();


        d.operate(4,2);
        a.operate(4,2);
        s.operate(4,2);
        m.operate(4,2);
    }
}

