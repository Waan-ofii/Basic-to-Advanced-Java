class Student {
    String id;
    String name;
    //parameterized constructor
    public Student (String i, String n) {
        id = i;
        name = n;
    }
        public void display() {
            System.out.println(id + " " + name);
        }
        public static void main(String args[]) {
        Student s1 = new Student("111/05", "John Tom");
        Student s2 = new Student("222/05", "Abebe Bekele");

        s1.display();
        s2.display();
    }
}