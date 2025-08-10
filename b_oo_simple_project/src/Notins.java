public class Notins {
    static int add(int a,int b) {
        return a + b;
    }
        static double add(double a,double b){
            return a-b;
        }

}
class poly{
    static int add(int a,int b,int c){
        return a+b+c;
    }
}
class runt{
    public static void main(String[] args) {
        System.out.println(Notins.add(2,3));
        System.out.println(Notins.add(5.0,2.0));
        System.out.println(poly.add(1,2,3));
       // poly nn=new poly();optinal but we don't need declare ;ike this
       // System.out.println(nn.add(1,2,3));

    }
}