package JavaAssociation2.Aggregation;

public class Employee {
    private String fname;
    private String lname;

    public Employee(String fname, String lname) {
        this.fname = fname;
        this.lname = lname;
    }

    public String getFname() {
        return this.fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return this.lname; //to avoid ambuiguity
    }

    public void setLname(String lname) {
        this.lname = lname;
    }
    @Override
    public String toString(){
        return this.fname+" "+this.lname;
    }
}
