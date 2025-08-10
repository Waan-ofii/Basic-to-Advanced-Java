public class commissionEmploye {
    private String firstname;
    private String lastname;
    private String SSN;
    private double GrossSAle;
    private double commonRate;

  public commissionEmploye(){
        firstname="abddi";
        lastname="qabnaa";
        SSN="Soo3";
        GrossSAle=1200.0;
        commonRate=12.5;
    }
  public  commissionEmploye(String fn,String ln,String sn,double gs,double cr){
      this.firstname=fn;
      this.lastname=ln;
      this.SSN=sn;
      this.GrossSAle=gs;
      this.commonRate=cr;
  }
public void setfirstname(String fnn){
      firstname=fnn;
}
public void setLastname(String lss){
      lastname=lss;
}
public void setSSN(String snn){
      SSN=snn;
}
public void setGrossSAle(double gr){
      GrossSAle=gr;
}
public void setCommonRate(double crr){
      commonRate=crr;
}
public double Earning(){
      return GrossSAle*commonRate;
}
public void Display(){
    System.out.println("firstname : "+firstname);
    System.out.println("lastname : "+lastname);
    System.out.println("SNN : "+SSN);
    System.out.println("GrossRate : "+GrossSAle);
    System.out.println("commonrate : "+commonRate);
}

}
class BaseCommission extends commissionEmploye{
    private double salary;
   BaseCommission(){
        salary=14000;
    }
    BaseCommission(String FN,String LN,String SNn,double GA,double CR,double SL ){
       super(FN,SNn,LN,GA,CR);
       salary=SL;
    }
    public void setSalary(double sl){
salary=sl;
    }
    double getSalary(){
       return salary;
    }
    public double Earning() {
        return super.Earning()+salary;
    }
    public void Display(){
       super.Display();
        System.out.println("salary : "+salary);
    }
}
class Torun{
    public static void main(String[] args) {
        BaseCommission bc=new BaseCommission();
        bc.Display();
        double earn= bc.Earning();
        System.out.println("total earned : "+earn);
    }
}