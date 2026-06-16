package JavaAssociation2.Aggregation;

public class Player {
    private String fname;
    private String lname;

    public Player(String fname,String lname) {
        this.fname = fname;
        this.lname=lname;
    }

    public String getFname() {
        return this.fname;
    }
    public String getLname() {
        return this.lname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }
    public void setLname(String lname) {
        this.lname = lname;
    }
    @Override
    public String toString(){
        return this.fname+" "+this.lname;
    }
}
