package Association.Address_Person;

public class Main {
    public static void main(String[] args) {
        Address a=new Address("234","radha street","chennai","tamilnadu","india","606909");
        Person p1=new Person("yuva","priya",a);
        Person p2=new Person("gokul","nathan",a);

        System.out.println(p1);
        System.out.println(p2);

        //a can be re-used in both persons
    }
}
