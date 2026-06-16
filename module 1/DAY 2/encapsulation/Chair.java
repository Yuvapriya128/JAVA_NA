// package oop.encapsulation;

public class Chair {
    private String material;
    private String color;
    private int weight;
    public Chair(String material,String color,int weight) {
        this.material=material;
        this.color=color;
        this.weight=weight;
    }

    public void adjustHeight(){
        System.out.println("Height is adjusted");
    }
    public String getDetails(){
       return (material+" "+color+" "+weight+" kg");
    }
}
