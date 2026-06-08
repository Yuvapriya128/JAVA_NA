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
