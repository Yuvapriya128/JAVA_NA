package carSIOC;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

 @Component
 public class Car {

        @Autowired
        @Qualifier("petrol")
        private Engine engine;

        @Autowired
        @Qualifier("bose")
        private MusicSystem musicSystem;

        public void drive() {
            engine.horsepower(150);
            musicSystem.digital();
            System.out.println("Car is Moving");
        }

    }
