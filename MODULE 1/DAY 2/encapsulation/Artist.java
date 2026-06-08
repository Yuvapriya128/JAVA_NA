// package oop.encapsulation;

public class Artist{
    protected  String name;
    protected String artStyle;
    protected String medium;
    public Artist(String name,String artStyle,String medium){
        this.name=name;
        this.artStyle=artStyle;
        this.medium=medium;
    }
    public void create(){
        System.out.println("Art is created");
    }
    public void getDetails(){
        System.out.println(name+" "+artStyle+" "+medium);

    }
}