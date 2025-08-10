public class man {
    public void play(){
        System.out.println("the man play what hthey love");
    }
}
class wemon extends man{
   public void play(){
       System.out.println("ovirided");
    }

    public static void main(String[] args) {
        wemon n=new wemon();
        n.play();
      man  b=new man();
        b.play();
    }
}

