public class Bicycle{
    public int gear;
    public int speed;


    Bicycle(int startgear, int speedup) {
        gear = startgear;
        speed=speedup;
    }
    void setgear(int newValue){
        gear=newValue;
    }
    void ApplyBrake(int decrement){
        speed-=decrement;
    }
    void Applyspeed(int Increment){
        speed+=Increment;
    }

}
 class Motorbike extends Bicycle {
    public int seatheight;
    public Motorbike(int startHeight,int startGear,int startSpeed){
        super(startSpeed,startGear);
        seatheight=startHeight;

    }

     public Motorbike() {
         super(4,6);
     }

     void setHieght(int newvalue){
        seatheight=newvalue;
    }

    public static void main(String[] args) {
        Bicycle bike=new Bicycle(2,3);
        Motorbike mb=new Motorbike();
    }
}
