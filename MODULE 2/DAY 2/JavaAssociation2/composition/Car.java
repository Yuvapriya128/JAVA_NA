package JavaAssociation2.composition;

public class Car {
    /*Insider Car only engine is created, so engine can't be used by any other
    if Car gets destroyed engine also gets destroyed as it's tightly coupled
    *
    * */
    private String brand;
    private Engine engine;

    public Car(String brand,int horsepower) {
        this.brand = brand;
        this.engine=new Engine(horsepower);
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
    public void start(){
        System.out.println("Car is starting");
        engine.start();
    }
}
