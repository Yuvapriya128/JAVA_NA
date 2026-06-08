
// Car (make,model,year,start(),stop(),getDetails())
// package oop.encapsulation;

public class Car {
   private String model;
   private String make;
   private int year;

   public Car(String model,String make,int year){
    this.model=model;
    this.make=make;
    this.year=year;
   }
   public void start(){
    System.out.println("Car started");
   } 
   public void stop(){
    System.out.println("Car stopped");
   }
   public String getDetails(){
    return (this.model+" "+this.make+" "+this.year+" year model");
   }
}
