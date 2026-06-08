package covariant;

public class Main {
    public static void main(String[] args) {
        Person p=new Person("yuva","priya");
        Student s=new Student("gokul","nathan","cs");
        Person ps=new Student("siva","nadham","agri");
        p=p.getDemo();
        s=s.getDemo();
        ps=ps.getDemo();

        System.out.println(p.getName());
        System.out.println(s.getName());
        System.out.println( ps.getName());
    }
}
