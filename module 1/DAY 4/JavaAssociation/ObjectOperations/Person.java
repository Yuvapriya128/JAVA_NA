package ObjectOperations;

public class Person {
    protected String fname;
    protected String lname;
    public Person(String fname,String lname){
        this.fname=fname;
        this.lname=lname;
    }
    public String getName(){
        return (fname+" "+lname);
    }
    public Person getDemo(){
        return new Person("yang","chan");
    }
    @Override
    public String toString(){
        return (fname+" "+lname);
    }

    @Override
    public int hashCode(){
        return fname.hashCode()+lname.hashCode();
    }

    @Override
    public boolean equals(Object o){
        Person p1=(Person)o;
        return this.fname.equalsIgnoreCase(p1.fname)  ;
    }

    public static void main(String[] args) {
        Person p=new Person("siva","priya");
        System.out.println(p.getName());
        System.out.println(p.getDemo());

        System.out.println(p.hashCode());

        Person p2=new Person("siva","priya");
        System.out.println(p2.hashCode());
        System.out.println(p.equals(p2));
        Person p3=p;
        System.out.println("p1 == p2 as i override equals for fname and lname");
        System.out.println(p.equals(p2)); //should be true as i override equals class
        System.out.println(p==p3);

        System.out.println(p.getClass().getSimpleName());
        System.out.println(p.getClass().getName());



    }
}
