package covariant;

public class Student extends Person{
    protected String course;

    public Student(String fname,String lname,String course){
        super(fname,lname);
        this.course=course;

    }
    public String getCourse(){
        return course;
    }

    @Override
    public Student getDemo(){
        return new Student("amu","dha","commerce");
    }

}
