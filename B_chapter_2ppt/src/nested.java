public class nested {
    void outermethod(){
        System.out.println("this is outer method ");
    }
    void Common(){
        System.out.println("this is common method fro  outer");
    }

    class innerclass{

        void Common(){
            System.out.println("common from inner");
        }
        void inner_method(){
            System.out.println("this is inner method ");
            this.Common();
            nested.this.Common();
        }



    }

    public static void main(String[] args) {
        nested bb=new nested();
        bb.outermethod();
        nested.innerclass inn=bb.new innerclass();
        inn.inner_method();


    }
}
