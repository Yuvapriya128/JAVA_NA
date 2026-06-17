package main;

import config.TodoSpringConfig;
import controller.CustomerConsoleController;
import controller.ProdConsoleController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class CustomerMainJdbc {
    public static void main(String[] args) throws SQLException {
        ApplicationContext context=new AnnotationConfigApplicationContext(TodoSpringConfig.class);
        CustomerConsoleController customerConsoleController=context.getBean("custcontrollerjdbc",CustomerConsoleController.class);

        customerConsoleController.welcome();
        customerConsoleController.showMenu();

    }
}
