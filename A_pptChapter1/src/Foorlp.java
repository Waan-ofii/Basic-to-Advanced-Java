import java.util.Scanner;

public class Foorlp {
    public static void main(String[] args) {
        int siz;
        Scanner kk = new Scanner(System.in);
        System.out.print("please enter the size of your array : ");
        siz = kk.nextInt();
        int[] arr = new int[siz];
        System.out.println("please enter element of your arry : ");
        for (int i = 0; i < siz; i++) {
            arr[i] = kk.nextInt();
        }
        System.out.println("here is your Element ");
        for(int i:arr){
            System.out.println(i);
        }
        int min = arr[0];
        for (int i = 0; i < siz; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }

        }
        System.out.println("Mininum number in the array is : "+min);
        int max = arr[0];
        for (int i = 0; i < siz; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
            System.out.println("Maximum number in the array is : " + max);

            System.out.println("\n");
            int odd = 0, even = 0;
            System.out.println("display all odd number : ");
            for (int i = 0; i < siz; i++) {
                if (arr[i] % 2 != 0) {
                    System.out.print(arr[i] + " , ");
                    odd++;
                }
            }
            System.out.println();
            System.out.println("display all even number : ");
            for (int i = 0; i < siz; i++) {
                if (arr[i] % 2 == 0) {
                    System.out.print(arr[i] + " , ");
                    even++;
                }
            }
            System.out.println("\n");
            System.out.println("here is : " + odd + " odd number");
            System.out.println("here is : " + even + " even number");

        }
    }
