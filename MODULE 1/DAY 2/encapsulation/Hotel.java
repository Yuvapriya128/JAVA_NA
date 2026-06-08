// package oop.encapsulation;
public class Hotel{
    protected String name;
    protected String location;
    protected String rating;

    public Hotel(String name,String location,String rating){
        this.name=name;
        this.location=location;
        this.rating=rating;
    }
    public void bookRoom(){
        System.out.println("Room booked");
    }
    public void getter(){
        System.out.println(name+" "+location+" with "+rating+" rating");
    }
}