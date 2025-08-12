package static_poly;
//static poly use system of overloading
public class St_poly {
    public void Add(int a,int b){
        System.out.println(a+b);
    }

    public void Add(int a,int b,int c){
        System.out.println(a+b+c);
    }

    public void Add(int a,double b){
        System.out.println(a+b);
    }
    public void Add(int a,long b){
        System.out.println(a+" "+b);
    }
    public void Add(String s,long h){
        System.out.println(s+h);
    }


}


class main {
    public static void main(String[] args) {

        St_poly s=new St_poly();
        s.Add(6,4);
        s.Add(6,4);
        s.Add(6,4,5);
        s.Add(6,0.5);
        s.Add("hello number" ,1);
    }



}
