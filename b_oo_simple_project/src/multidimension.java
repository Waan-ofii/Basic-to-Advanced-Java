import java.util.Scanner;

public class multidimension {
    public static void main(String[] args) {
        int row=3;
        int col=3;
        int[][] a=new int[row][col];
        Scanner bb=new Scanner(System.in);
        System.out.println("please enter elelemts of your array");
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                System.out.print("please eneter ["+i+"]["+"["+j+"] :");
               a[i][j]= bb.nextInt();
            }
        }


        System.out.println("here is yor element : ");
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                System.out.print(a[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println("largest of matrix :");
        int largest=a[0][0];
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                if(a[i][j]>largest){
                    largest=a[i][j];
                }
            }

        }
        System.out.println("largest is : "+largest);



    }
}
