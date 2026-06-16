package carSIOC;

import org.springframework.stereotype.Component;

@Component("petrol")
public class PetrolEngine implements Engine {

    @Override
    public void horsepower(int hp) {
        System.out.println("Petrol Engine Running with " + hp + " HP");
    }
}