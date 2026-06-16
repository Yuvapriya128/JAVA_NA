package CarSIOC;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
@ComponentScan(basePackages = "CarSIOC")
public class Main {

    @Bean
    public Scanner getSc() {
        return new Scanner(System.in);
    }

    public static void main(String[] args) {

        ApplicationContext context =
                new AnnotationConfigApplicationContext(Main.class);

        CarConsoleController controller =
                context.getBean(CarConsoleController.class);

        controller.showMenu();
    }
}