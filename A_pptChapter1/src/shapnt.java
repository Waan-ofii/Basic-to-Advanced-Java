public class shapnt {
    public static void main(String[] args){
        int n=6;
        int z=1;
        for(int i=1; i<=n; i++){
            for(int j=n-1; j>=i; j--){
                System.out.print(" ");
            }
            for(int k=1; k<=z; k++){
                System.out.print("*");
            }
            z+=2;
            System.out.println();
        }


        int s=5;
        int x=9;
        for(int a=1;  a<=s; a++){
            for(int b=1; b<=a; b++){
                System.out.print(" ");
            }
            for(int k=1; k<=x;  k++ ){
                System.out.print("*");
            }
            x-=2;
            System.out.println();
        }
    }
}
