
// package oop.encapsulation;

public class Doctor{
    private String name;
    private int exp;
    private String specialization ;

    public Doctor(String name,int exp, String specialization){
        this.name=name;
        this.exp=exp;
        this.specialization=specialization;

    }    
    public void diagnose(){
        System.out.println("Patient is diagnosed");
    }
    public void getter(){
        System.out.println(name+" "+exp+" experience in "+specialization);
    }
    

}
