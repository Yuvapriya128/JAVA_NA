package covariant;

public class Person {
    protected String fname;
    protected String lname;
    public Person(String fname,String lname){
        this.fname=fname;
        this.lname=lname;
    }
    public String getName() {
        return (fname+" "+lname);
    }
    public Person getDemo(){
        return  new Person("jack","jon");
    }
}
