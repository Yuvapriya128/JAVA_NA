package Association.Address_Person;


public class Person {
    private String fname;
    private String lname;
    Address address;

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

    public Person(String fname, String lname,Address address){
        this.fname=fname;
        this.lname=lname;
        this.address=address;

    }
    public String toString(){
        return (fname+" "+lname+"\n "+address);
    }

}