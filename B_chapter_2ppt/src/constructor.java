import java.util.Scanner;

public class constructor {
    private String name;
    private  String department;
    private int age;
    static String unev="JImma";
    static{
        System.out.println("static block");
    }

    constructor(){
        System.out.println("this default constructor ");
        name="";
        department="";
        age=0;
    }

    constructor(String nam,String dept,int ag){
        this.name=nam;
        this.department=dept;
        this.age=ag;


        System.out.println("this is parametrized constructor ");
    }
    constructor(constructor s2){
        this.name=s2.name;
        this.department=s2.department;
        this.age=s2.age;

    }
    void register(){
        System.out.println("pease enter student Information ");
        Scanner bb=new Scanner(System.in);
        System.out.println("please enter name : ");
       name= bb.nextLine();
        System.out.println("please enter deparment : ");
       department= bb.nextLine();
        System.out.println("please enter age : ");
        age=bb.nextInt();

    }

    void Display(){
        System.out.println(" name : "+name);
        System.out.println(" department : "+department);
        System.out.println(" age : "+age);
        System.out.println("university : "+unev);
        System.out.println();
    }
    {
        System.out.println("instance block ");
    }

    public static void main(String[] args) {
        constructor s1=new constructor();
        s1.register();
        s1.Display();
        constructor s2=new constructor("tola","css",22);
        s2.Display();
        constructor s3=new constructor(s2);
        s3.Display();

    }
}


