package oop.inheritance;

public class Student extends Person{

    
    protected String grade;
    protected String school;

    public Student(String grade,String school,String fname,String lname,int age){
        super(fname,lname,age);
        this.grade=grade;
        this.school=school;
        // super(fname,lname,age); super should be used before this attributes
    }


    public String getter(){
        return (super.fname+" "+this.grade+" "+this.school);
    }
    public String getDetails(){
        return (super.fname+" "+super.lname+"\n"+this.age+" years old \n"+this.grade+" grade in "+school);
    }

    public void study(){
        System.out.println(this.fname+" is studying in "+this.grade+" in "+this.school);

    }

    public void getReportCard(){
        System.out.println(super.fname+" is studying in "+this.grade+" in "+this.school);     
    }
}