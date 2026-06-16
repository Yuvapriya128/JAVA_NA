package MyCollections;

public class Person {
    private String fname;
    private String lname;
    private int age;

    public Person(String fname, String lname, int age) {
        this.fname = fname;
        this.lname = lname;
        this.age = age;
    }
    @Override
    public String toString(){
        return ("\n"+this.fname+" "+this.lname+" "+this.age);
    }

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    @Override
    public int hashCode(){
        return this.fname.hashCode()+this.lname.hashCode();
    }
    @Override
    public boolean equals(Object o){
        if(this==o){
            return true;
        }
        if(!(o instanceof Person)){
            return false;
        }
        Person p=(Person)o;

        return this.age==p.age && this.fname.equalsIgnoreCase(p.fname) && this.lname.equalsIgnoreCase(p.lname);

    }
}
