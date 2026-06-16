package oop.inheritance;

public class Main{
    public static void main(String[] args){
        Person sp=new Student("O","Antonys","jackie","chan",50);
        System.out.println( ((Student)(sp)).getDetails());

        System.out.println("-----------");

        Student stu=new Student("O","Antonys","jackie","chan",50);
        System.out.println(stu.getDetails());

        System.out.println("-----------");
        Employee emp=new Employee("jackie", "chan", 50, "Manager", 50000,102);
        System.out.println(emp.getter());

        System.out.println("-----------");
        Manager mg=new Manager("siva", "priya", 30, "HR", 5000, 120, "HR team");
        System.out.println(mg.getter());

        

        

        
        
    }
}