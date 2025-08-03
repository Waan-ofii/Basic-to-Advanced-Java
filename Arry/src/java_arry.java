import java.util.Scanner;

public class java_arry {
    public static void main(String[] args) {


        Scanner n = new Scanner(System.in);
       /*
       System.out.println("please enter arrays size : ");
       int size=n.nextInt();

        int[] num=new int[size];
        for(int i=0; i<size; i++){
            System.out.println("eneter a number :");
            num[i]=n.nextInt();

        }
        System.out.println("HERE IS YOUR NUMBER ");
        for(int i=0; i<size; i++){
            System.out.println(num[i]);
        }
        */
        System.out.println("please enter arrays size : ");
        int size = n.nextInt();

        String[] num = new String[size];
        for(int i=0; i<size; i++){
            System.out.println("enter a name : " );
            n.nextLine();
        }
        System.out.println("HERE IS YOUR Name ");
        for (int i = 0; i < size; i++) {
            System.out.println(num[i]);
        }
    }
}
https://github.com/Waan-ofii/Data-structre-and-Algorithm.git



echo "# Data-structre-and-Algorithm" >> README.md
git init
git add README.md
git commit -m "first commit"
git branch -M main
git remote add origin https://github.com/Waan-ofii/Data-structre-and-Algorithm.git
git push -u origin main

git remote add origin https://github.com/Waan-ofii/Data-structre-and-Algorithm.git
git branch -M main
git push -u origin main

