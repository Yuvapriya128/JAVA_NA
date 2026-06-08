package upcast;

public class Employee extends Person {
    protected String fname;
    protected String lname;
    protected String empid;
    public Employee(String fname,String lname,String empid){
        super(fname,lname);
        this.empid=empid;
    }
    public String getName(){
        return (super.fname+" "+super.lname+" "+ empid+" empid");
    }
}