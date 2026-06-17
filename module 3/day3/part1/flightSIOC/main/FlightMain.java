package flightSIOC.main;

import flightSIOC.config.FlightConfig;
import flightSIOC.controller.FlightConsoleController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/*Suppose tomorrow your company says:

Flight number must start with "AI" or "IND"
Departure date cannot be in the past
Arrival date must be after departure date
Seats must be greater than 0

To do these : add service layer
daoimpl should contain only: crud operations
*/
public class FlightMain {
    public static void main(String[] args) {
        ApplicationContext context=new AnnotationConfigApplicationContext(FlightConfig.class);

        FlightConsoleController fcc=context.getBean(FlightConsoleController.class);
        fcc.welcome();
        fcc.showMenu();
//        Before this create table
//        check dependency and sync projects
    }
}
