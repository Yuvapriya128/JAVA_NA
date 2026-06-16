// package oop.encapsulation;

public class LightBulb{
    private int watts;
    private String brand;

    public LightBulb(int watts, String brand){
        this.watts=watts;
        this.brand=brand;

    }
    public String getDetails(){
        return ("LightBulb is "+this.watts+" "+this.brand);
    }
    public void turnOn(){
        System.out.println("Light is turned ON");
    }
    public void turnOff(){
        System.out.println("Light is turned OFF");
    }

}
