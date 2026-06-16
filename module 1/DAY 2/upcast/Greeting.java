package upcast;

public class Greeting {
    public void  greet(){
        System.out.println("Greetings");
    }
//    public void  greet(Student s){
//        System.out.println("Greetings Student "+s.getName());
//    }
    public void  greet(Person p){
        System.out.println("Greetings Person: "+p.getName());
    }
//    public void  greet(Employee e){
//        System.out.println("Greetings Employee: "+e.getName());
//    }

    //null can be assigned to:
/*
Person
Student
Employee

Java tries to find the most specific method.

But Student and Employee are siblings.

Neither is more specific than the other.
    */
    public static void main(String[] args) {
        Person p=new Person("siva","priya");
        Student s=new Student("jackie","chan","antony");
        Employee e=new Employee("sundar","ram","123");
        Greeting g=new Greeting();
        g.greet(p);
        g.greet(s);
        g.greet();
        g.greet(e);
//        g.greet(null);
    }
}
