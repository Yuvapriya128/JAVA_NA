package oop.inheritance;

public class Person {
    protected String fname;
    protected String lname;
    protected int age;

    public Person(String fname,String lname,int age){
        this.fname=fname;
        this.lname=lname;
        this.age=age;
    }
    public String getFname(){
        return this.fname;
    }
    public String getLname(){
        return this.lname;
    }
    public int getAge(){
        return this.age;
    }

    public void eat(){
       System.out.println(this.fname+" "+this.lname+" is eating");
    }
    
    public void walk(){
       System.out.println(this.fname+" "+this.lname+" is walking");
    }

    public void talk(){
       System.out.println(this.fname+" "+this.lname+" is talking");
    }
}
