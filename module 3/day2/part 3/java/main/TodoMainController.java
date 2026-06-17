package main;

import config.TodoSpringConfig;
import controller.TodoConsoleController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class TodoMainController {
    public static void main(String[] args) throws SQLException {
        ApplicationContext context=new AnnotationConfigApplicationContext(TodoSpringConfig.class);
        TodoConsoleController todoConsoleController=context.getBean(TodoConsoleController.class);
        todoConsoleController.welcome();
        todoConsoleController.showMenu();
    }
}
