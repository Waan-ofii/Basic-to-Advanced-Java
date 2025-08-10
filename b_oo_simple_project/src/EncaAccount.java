public class EncaAccount {
    private double balance;
    private String AccNo;


    public double getbalance(){
        return balance;
    }

    public void setbalance(double bal){
        this.balance=bal;//explicit use of this
    }
    public String getAccno(){
        return AccNo;
    }

    public void setAccNo(String Acc){
        AccNo=Acc;//implicit use of this
    }

}
class runner{
    public static void main(String[] args) {
        EncaAccount c1=new EncaAccount();
        c1.setbalance(1000100);
        c1.setAccNo("1000454348392");

        EncaAccount c2=new EncaAccount();
        c2.setbalance(2000200);
        c2.setAccNo("100002223312");

        System.out.println("balance customer c1 :"+c1.getbalance());
        System.out.println("accno c1 :"+c1.getAccno());
        System.out.println("balance customer c2 :"+c2.getbalance());
        System.out.println("acc no c2 :"+c2.getAccno());

    }
}