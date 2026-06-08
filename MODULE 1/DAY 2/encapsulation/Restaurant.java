// package oop.encapsulation;
public class Restaurant{
    protected String name;
    protected String cuisine;
    protected float rating;

    public Restaurant(String name,String cuisine,float rating){
        this.name=name;
        this.cuisine=cuisine;
        this.rating=rating;
    }
    public void orderFood(){
        System.out.println("Food ordered");
    }
    public void getDetails(){
        System.out.println(name+" "+cuisine+" "+rating+" rating");
    }

}