public class array {
    public static void main(String [] args){
        //declaring arrays
        int[] num={2,3,4,7,5,32};
        System.out.println("accessing array");

        System.out.println(num[0]);

        num[0]=44;
        System.out.println("changing arrays elment");
        System.out.println(num[0]);

        System.out.println("arrays length");
        System.out.println(num.length);

        System.out.println("access all element of array by using array length in for loop");
        for(int i=0; i<num.length; i++){
            System.out.println(num[i]);
        }
    }
}
