package main;

import config.TodoSpringConfig;
import controller.CustomerConsoleController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class CustomerMainCollections {
    public static void main(String[] args) throws SQLException {
        ApplicationContext context=new AnnotationConfigApplicationContext(TodoSpringConfig.class);
        CustomerConsoleController customerConsoleController=context.getBean("custcontroller",CustomerConsoleController.class);

        customerConsoleController.welcome();
        customerConsoleController.showMenu();

    }
}
