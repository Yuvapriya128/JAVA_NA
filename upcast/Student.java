package upcast;

public class Student extends Person {
    protected String fname;
    protected String lname;
    protected String school;
    public Student(String fname,String lname,String school){
        super(fname,lname);
        this.school=school;
    }
    public String getName(){
        return (super.fname+" "+super.lname+" "+ school+" school");
    }
}
