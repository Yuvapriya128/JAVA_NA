package bookSIOC.main;

import bookSIOC.config.BookConfig;
import bookSIOC.controller.BookConsoleController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BookMain {
    public static void main(String[] args) {
        ApplicationContext context=new AnnotationConfigApplicationContext(BookConfig.class);
        BookConsoleController controller=context.getBean(BookConsoleController.class);

        controller.welcome();
        controller.showMenu();
    }
}
