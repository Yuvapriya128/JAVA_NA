// package oop.encapsulation;

class Normal {
    private String fname;
    private String lname;
    private int age;

    public Normal(String fname,String lname,int age){
        this.fname=fname;
        this.lname=lname;
        this.age=age;
    }
    public String getter(){
        return (this.fname+ " "+this.lname +" is "+this.age);
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
