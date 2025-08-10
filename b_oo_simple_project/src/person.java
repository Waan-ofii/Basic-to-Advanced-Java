import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

class Person {
    String name;
    String address;
    String email;
    int phoneNo;

    Person(String name, String address, String email, int phoneNo) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.address = address;
    }

    void display() {
        System.out.println("Name     : " + name);
        System.out.println("Address  : " + address);
        System.out.println("Email    : " + email);
        System.out.println("Phone No : " + phoneNo);
    }
}

class student extends Person {
    String status;

    student(String status, String name, String address, String email, int phoneNo) {
        super(name, address, email, phoneNo);
        this.status = status;
    }

    @Override
    void display() {
        super.display();
        System.out.println("Status   : " + status);
    }
}

class Employee extends Person {
    String office;
    double salary;
    Date date;

    Employee(String office, double salary, Date date, String name, String address, String email, int phoneNo) {
        super(name, address, email, phoneNo);
        this.office = office;
        this.salary = salary;
        this.date = date;
    }

    @Override
    void display() {
        super.display();
        System.out.println("Office   : " + office);
        System.out.println("Salary   : " + salary);
        System.out.println("Date     : " + date);
    }
}

class Faculty extends Employee {
    String officeHours;
    int rank;

    Faculty(String officeHours, int rank, String office, double salary, Date date, String name, String address, String email, int phoneNo) {
        super(office, salary, date, name, address, email, phoneNo);
        this.officeHours = officeHours;
        this.rank = rank;
    }

    @Override
    void display() {
        super.display();
        System.out.println("Office Hours : " + officeHours);
        System.out.println("Rank         : " + rank);
    }
}

class Staff extends Employee {
    String title;

    Staff(String title, String office, double salary, Date date, String name, String address, String email, int phoneNo) {
        super(office, salary, date, name, address, email, phoneNo);
        this.title = title;
    }

    @Override
    void display() {
        super.display();
        System.out.println("Title    : " + title);
    }
}

 class Runner2 {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

      /*  Person p = new Person("Tola", "Jimma", "toli@gmail.com", 64532212);
        p.display();
        System.out.println();

        student s = new student("Second", "Bona", "Chancho", "toli@gmail.com", 44332266);
        s.display();
        System.out.println();

        Employee e = new Employee("JU", 45000, sdf.parse("05-05-2025"), "Chaltu", "Sululta", "chal@gmail.com", 66443321);
        e.display();
        System.out.println();

        Faculty f = new Faculty("8 hours", 85, "JU", 1245000, sdf.parse("05-05-2025"), "Chaltu", "Sululta", "chal@gmail.com", 66443321);
        f.display();
        System.out.println();*/

        Staff r = new Staff("Administrator", "JU", 45000, sdf.parse("05-05-2025"), "Bontu", "Sululta", "chal@gmail.com", 66443321);
        r.display();
    }
}
