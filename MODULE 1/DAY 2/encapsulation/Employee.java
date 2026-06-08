// package oop.encapsulation;

public class Employee {
    private String name;
    private String position;
    private int salary;

    public Employee(String name, String position,int salary){
        this.name=name;
        this.position=position;
        this.salary=salary;
    }
    public String getter(){
        return(this.name+" "+this.position+" "+this.salary+"rupees");
    }
    public void work(){
        System.out.println(this.name+" is working in "+this.position);
    }
    public void getDetails(){
        System.out.println(this.name+" is getting "+this.salary);

    }
}
