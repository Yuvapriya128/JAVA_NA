// Laptop (brand,model,processor,ram,storage,turnOn(),turnOff(),getDetails)
// package oop.encapsulation;

public class Laptop {
    private String brand;
    private String model;
    private String proc;
    private String ram;
    private String storage;

    public Laptop(String brand,String model,String proc,String ram,String storage){
        this.brand=brand;
        this.model=model;
        this.proc=proc;
        this.ram=ram;
        this.storage=storage;
    }

    public void turnOn(){
        System.out.println("Laptop is turned On");
    }
    public void turnOff(){
        System.out.println("Laptop is turned Off");
    }
    public String getDetails(){
        return (brand+" "+model+" "+proc+" "+ram+" "+storage);
    }
}
