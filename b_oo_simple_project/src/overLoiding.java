public class overLoiding {
    public void yes(){
        System.out.println("yes");
    }
    public void yes(int  a, int b){
        System.out.println("yes : "+(a+b));
    }
    public int yes(int a,int b,int c){
        return a+b+c;
    }
}
class dest{
    public static void main(String[] args) {
        overLoiding o=new overLoiding();
        o.yes();
        o.yes(2,3);
        System.out.println("nope :"+o.yes(4,5,6));
    }
}

