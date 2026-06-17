package main;

import config.TodoSpringConfig;
import controller.ProdConsoleController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class ProductMainJdbc {
    public static void main(String[] args) throws SQLException {
        ApplicationContext context=new AnnotationConfigApplicationContext(TodoSpringConfig.class);
        ProdConsoleController prodConsoleController=context.getBean("prodcontrollerjdbc",ProdConsoleController.class);

        prodConsoleController.welcome();
        prodConsoleController.showMenu();

    }
}
