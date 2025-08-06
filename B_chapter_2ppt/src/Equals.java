public class Equals {
    public static void main(String[] args) {
        tola t=new tola(7);
        chala c=new chala(7);

        System.out.printf("is object are equal : "+t.equals(c));
    }
}
class tola{
    int f;
    tola(int x) {
        this.f = x;
    }


}
class chala{
    int g;
    chala(int k){
        this.g=k;
    }
}
