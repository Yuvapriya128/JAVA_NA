package ObjectOperations;

public class Student extends Person {

    protected String school;
    public Student(String fname,String lname,String school){
        super(fname,lname);
        this.school=school;
    }
    public String getDetails(){
        return (super.fname+" "+super.lname+" "+ school+" school");
    }
    public Student getDemoObject(){
        return new Student("yas","min","be");
    }

    @Override
    public String toString(){
        return ("name: "+fname+" "+lname);
    }

    @Override
    public int hashCode(){
        return fname.hashCode()-lname.hashCode();
    }

    @Override
    public boolean equals(Object o){
        Student stud=(Student)o;
        return stud.fname.equalsIgnoreCase(stud.fname) && stud.lname.equalsIgnoreCase(stud.lname);
    }

    public static void main(String[] args) {
        Student s=new Student("arul","kumar","mti");
        Person p=new Person("arul","kumar");
        Person ps=new Student("arul","murugan","cse");

        System.out.println(s.getDetails());
        System.out.println(p.getName());
        System.out.println(((Student)(ps)).getDetails());

//        System.out.println(s.equals(p)); as in equal i tried to cast the object
//        to (Student)o
        System.out.println(s.equals(ps));
        System.out.println(p.equals(ps));

        System.out.println(s.hashCode());
        System.out.println(p.hashCode());
        System.out.println(ps.hashCode());

        System.out.println(ps.getClass().getSimpleName());
        System.out.println(p.getClass().getSimpleName());
        System.out.println(s.getClass().getSimpleName());









    }
}
