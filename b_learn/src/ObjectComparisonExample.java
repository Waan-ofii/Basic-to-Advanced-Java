public class ObjectComparisonExample {


    public static void main(String[] args){
        Double x = new Double(988.77544);
        Long y = new Long(127689);



        System.out.println("Objects are not equal, hence it returns " + x.equals(y));
        System.out.println("Objects are equal, hence it returns " + x.equals(12));
    }
}
