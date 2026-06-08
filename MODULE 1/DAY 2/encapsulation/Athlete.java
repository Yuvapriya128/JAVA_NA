// package oop.encapsulation;

public class Athlete{
    protected String name;
    protected String sport;
    protected String team;
    public Athlete(String name,String sport,String team){
        this.name=name;
        this.sport=sport;
        this.team=team;
    }
    public void train(){
        System.out.println("Athlete is training");
    };
    public void compete(){
        System.out.println("Athlete is competing");
    };
    public void getDetails(){
        System.out.println(name+" "+sport+" "+team);
    };
}