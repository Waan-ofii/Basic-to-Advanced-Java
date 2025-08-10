public class circle {
    double withdraw;
double diposit;

    circle(){
        withdraw=0;
        diposit=0;

    }
    circle(double r ,double p){
        withdraw=r;
        diposit=p;
    }
    void setWithdraw(double with){
        withdraw=with;
        diposit=diposit-withdraw;
        System.out.println("dip after withdrawal  "+diposit);
    }
    void setDiposit(double dips){
        diposit=dips;
        System.out.println("ddddd "+diposit);
    }

    public static void main(String[] args) {
        circle vv=new circle();
        vv.setDiposit(3000);
        vv.setWithdraw(300);
       circle v2=new circle(4,3.14);
        System.out.println("diposit : "+v2.diposit);

    }
}
