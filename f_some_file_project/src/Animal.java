public class Animal {
    String name;
    String sound;
    int weight;


    public Animal() {
        System.out.println("this is defoult ");

    }

    public Animal(String name, String sound, int weight) {
        this.name = name;
        this.sound = sound;
        this.weight = weight;
    }

    public Animal(Animal a) {
        this.name = a.name;
        this.sound = a.sound;
        this.weight = a.weight;

    }
    void eat() {
        System.out.println("animal is eating ");
    }

    void walk() {
        System.out.println("animal is walking ");
    }
    void Display(){
        System.out.println("name : "+name);
        System.out.println("sound : "+sound);
        System.out.println("name : "+weight);
    }


 public class cat extends Animal {
        public cat(){
            System.out.println("defoult cat ");
        }
        public cat(String name,String sound,int weight){
            this.name = name;
            this.sound = sound;
            this.weight = weight;
        }
        String locaton;
//super(name,sound,weight);
        void eat() {
            System.out.println("cat is eating ");
        }

        void walk() {
            System.out.println("cat is walking ");
        }


        void Display() {
            System.out.println("location " + locaton);
        }
    }
    public static void main(String[] args) {
        cat c = new cat();
        c.name = "gtr";
        c.sound = "meow";
        c.weight = 20;
        c.locaton="jimmaa";
        c.Display();
        c.eat();
        c.walk();
        cat cr=new cat("jj","wof",43);
        cr.Display();

    }
}