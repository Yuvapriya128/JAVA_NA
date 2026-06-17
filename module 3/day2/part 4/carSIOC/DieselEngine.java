package carSIOC;


import org.springframework.stereotype.Component;

@Component("diesel")
public class DieselEngine implements Engine {

    @Override
    public void horsepower(int hp) {
        System.out.println("Diesel Engine Running with " + hp + " HP");
    }
}