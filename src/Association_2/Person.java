package Association_2;

public class Person {
    protected String fname;
    protected String lname;
    private Pancard pancard;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public Person(String fname, String lname,Pancard pancard) {
        this.fname = fname;
        this.lname = lname;
        this.pancard=pancard;
    }
    public String toString(){
        return (fname+" "+lname+" "+ pancard.toString());
    }

    public static void main(String[] args) {
        Pancard pan=new Pancard("WERT2345S","23-11-2008");
        Person person=new Person("siva","priya",pan);
        System.out.println(person);

    }
}
//1:1 can put person in pancard or viceversa
