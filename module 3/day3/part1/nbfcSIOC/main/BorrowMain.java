package nbfcSIOC.main;

import nbfcSIOC.config.SpringConfiguration;
import nbfcSIOC.controller.BorrowConsoleController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BorrowMain {
    public static void main(String[] args) {
        ApplicationContext context=new AnnotationConfigApplicationContext(SpringConfiguration.class);

        BorrowConsoleController control=context.getBean(BorrowConsoleController.class);

        control.welcome();
        control.showMenu();
    }
}
