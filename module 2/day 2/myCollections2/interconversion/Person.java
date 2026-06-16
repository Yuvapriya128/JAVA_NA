package MyCollections2.Interconversion;

import java.util.Objects;

public class Person {
    private String fname;
    private String lname;
    private int age;

    public Person(String fname, String lname, int age) {
        this.fname = fname;
        this.lname = lname;
        this.age = age;
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
    public String toString(){
        return (this.fname+" "+this.lname+" "+this.age);
    }
    @Override
    public boolean equals(Object o1){
        if(o1 instanceof Person) {
            Person p = (Person) o1;
            return this.age == p.getAge() && this.fname.equalsIgnoreCase(p.getFname()) &&
                    this.lname.equalsIgnoreCase(p.getLname());
        }
        else{
            return false;
        }
    }
    @Override
    public int hashCode(){
        return Objects.hash(this.fname+this.lname+this.age);
    }
}
