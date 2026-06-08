package oop.inheritance;

public class Employee extends Person {
     int empid;
     String position;
     int salary;

    public Employee(String fname,String lname,int age, String position,int salary,int empid){
        super(fname,lname,age);
        this.position=position;
        this.salary=salary;
        this.empid=empid;
    }
    public String getter(){
        return(super.fname+" "+super.lname+" "+super.age+""+this.position+" "+this.empid+" "+this.salary+"rupees");
    }
    public void work(){
        System.out.println(super.fname+" is working in "+this.position);
    }
    public void getDetails(){
        System.out.println(super.fname+" is getting "+this.salary);

    }

    public static void main(String[] args){
         Employee emp=new Employee("jackie", "chan", 50, "Manager", 50000);
        System.out.println(emp.getter());
        
    }
   
}
