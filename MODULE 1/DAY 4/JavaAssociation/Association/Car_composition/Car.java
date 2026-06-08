package Association.Car_composition;

public class Car {
    Engine engine;
    ac AC;
    MusicSystem ms;

    public Car(Engine engine, ac AC, MusicSystem ms) {
        this.engine = engine;
        this.AC = AC;
        this.ms = ms;
    }
    public String getDetails(){
        return ("Car:"+engine.getHorsepower()+" "+AC.getTons()+" "+ms.getBrand());
    }

    public static void main(String[] args) {
        Car car=new Car(new Engine(500),new ac(1),new MusicSystem("saregama"));
        System.out.println(car.getDetails());
    }

}
