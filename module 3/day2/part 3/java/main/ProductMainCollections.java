package main;

import config.TodoSpringConfig;
import controller.ProdConsoleController;
import dao.ProductDao;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public class ProductMainCollections {
    public static void main(String[] args) throws SQLException {
        ApplicationContext context=new AnnotationConfigApplicationContext(TodoSpringConfig.class);
        ProdConsoleController prodConsoleController=context.getBean("prodcontroller",ProdConsoleController.class);

        prodConsoleController.welcome();
        prodConsoleController.showMenu();

    }
}
