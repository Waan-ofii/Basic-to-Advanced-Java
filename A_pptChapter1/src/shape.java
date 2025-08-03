public class shape {
    public static void main(String[] args){


        System.out.println("T see how nested for loop is work ");


        for(int i=1; i<=3; i++){
            for(int j=1; j<=3; j++){
                System.out.println("("+i+""+j+")");
            }
            System.out.println();
        }
        System.out.println("Right_Triangle shape");


        for(int i=1; i<=5; i++){
            for(int j=1; j<=i; j++){
                System.out.print("*");
            }
            System.out.println();
        }

        System.out.println("Inverted Right_Triangle shape");
        for(int i=5; i>=1; i--){
            for(int j=1; j<=i; j++){
                System.out.print("*");
            }
            System.out.println();
        }
        int n=6;
        System.out.println("we will see the shape1 ");
        for(int i=1; i<=n;i++){
            for(int j=n-1; j>=i; j--){
                System.out.print(" ");
            }
            for(int k=1; k<=i; k++){
                System.out.print("@");
            }
            System.out.println();
        }

        int s=6;
        int z=1;
        System.out.println("we will see the shape2 ");
        for(int i=1; i<=s;i++){
            for(int j=s-1; j>=i; j--){
                System.out.print(" ");
            }
            for(int k=1; k<=z; k++){
                System.out.print("*");
            }
            z+=2;
            System.out.println();
        }

        System.out.println("to character");
int x=65;
        for(int i=1; i<=5; i++){
            for(int j=1; j<=i; j++){
                System.out.print((char)x);
            }
            x++;
            System.out.println();
        }
        System.out.println("1 12 123");
        for(int i=1; i<=5; i++){
            for(int j=1; j<=i; j++){
                System.out.print(j);
            }
            System.out.println();
        }
        System.out.println("ab");
        int y=65;
        for(int i=0; i<=5; i++){
            for(int j=0; j<=i; j++){
                System.out.print((char)(y+j));
            }
            System.out.println();
        }
    }
}
