public interface fly {
    void canfly();
}
interface eat{
    void cabEat();
}
class Bird {
void canWAlk(){
    System.out.println("bird can walk");
}
    }
    class duck extends Bird implements fly,eat{
      public  void canfly(){
            System.out.println("duck can fly");
        }
       public void cabEat(){
            System.out.println("duck canwalk");
        }

        public static void main(String[] args) {

            duck jj=new duck();
            jj.canfly();
            jj.canWAlk();
            jj.cabEat();

        }
    }
