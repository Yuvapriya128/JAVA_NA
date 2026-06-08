// package oop.encapsulation;

public class Student {
    private String name;
    private String grade;
    private String school;

    public String getter(){
        return (this.name+" "+this.grade+" "+this.school);
    }

    public Student(String name,String grade,String school){
        this.name=name;
        this.grade=grade;
        this.school=school;
    }

    public void study(){
        System.out.println(this.name+" is studying in "+this.grade+" in "+this.school);

    }

    public void getReportCard(){
        System.out.println(this.name+" is studying in "+this.grade+" in "+this.school);     
    }
}
