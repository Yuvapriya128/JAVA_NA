package Association.pass_in_person;

public class Person {
    private String fname;
    private String lname;
    Passport passportInPerson;

    public String getFname() {
        return fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getLname() {
        return lname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public Person(String fname, String lname,Passport passportInPerson){
        this.fname=fname;
        this.lname=lname;
        this.passportInPerson=passportInPerson;
    }
    public String toString(){
        return (fname+" "+lname+" "+passportInPerson.toString());
    }

}
