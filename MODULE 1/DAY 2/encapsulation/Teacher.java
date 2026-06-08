// package oop.encapsulation;

public class Teacher{
    protected String name;
    protected String subject;
    protected int experience;
    public Teacher(String name,String subject,int experience){
        this.name=name;
        this.subject=subject;
        this.experience=experience;
    }
    public void teach(){
        System.out.println(name+ " is teaching");
    }
    public void getDetails(){
        System.out.println(name +" is teaching "+subject+" with "+experience+" experience");
    }
}