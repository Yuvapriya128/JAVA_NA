package Association.per_in_passport;


public class Person {
    private String fname;
    private String lname;


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

    public Person(String fname, String lname){
        this.fname=fname;
        this.lname=lname;

    }
    public String toString(){
        return (fname+" "+lname);
    }

}

