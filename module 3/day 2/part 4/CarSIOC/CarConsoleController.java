package CarSIOC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CarConsoleController {

    @Autowired
    private Scanner sc;

    @Autowired
    private Car car;

    public void showMenu() {
        System.out.println("1.Start Car");
        System.out.println("2.Exit");
        do {
            int option = sc.nextInt();
            switch (option) {
                case 1 -> car.drive();
                case 2 -> {
                    System.out.println("Thank You");
                    return;
                }
                default -> System.out.println("Invalid Option");
            }

        } while (true);
    }
}