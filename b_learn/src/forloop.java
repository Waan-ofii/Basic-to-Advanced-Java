public class forloop {
    public static void main(String[] args) {


    for(int i=1; i<=5; i++)
    {
        for (int j = 5; j >= i; j--) {
            System.out.print("*");
        }
        System.out.println();
    }


    int ar[]={1,20,30,40};
    for(int i:ar){
        System.out.println(i);
    }
    }

}
