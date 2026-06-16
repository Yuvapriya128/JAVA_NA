package  oop.inheritance;

public class Manager extends Employee {
    String team;
    public Manager(String fname,String lname,int age,String position,int salary,int empid,String team)
    {
        super(fname,lname,age,position,salary,empid);
        this.team=team;
    }
    public String getter(){
        return(fname+" "+lname+"\n"+age+"\n"+position+"\n"+salary+"\n"+empid+"\n"+team+"team");

    }   



    
}
