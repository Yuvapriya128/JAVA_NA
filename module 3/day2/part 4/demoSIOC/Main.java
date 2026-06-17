package demoSIOC;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
@ComponentScan(basePackages = "demoSIOC")
public class Main {
    @Bean
    public Scanner getSc(){
        return new Scanner(System.in);
    }

    public static void main(String[] args) {

        ApplicationContext context=new AnnotationConfigApplicationContext(Main.class);

//        ExpenseManager expenseManager=context.getBean(ExpenseManager.class);
//        expenseManager.payGasBill(1400);
//        expenseManager.payHouseRent(150000);
//        expenseManager.payWaterBill(6000);

        ConsoleController consoleController=context.getBean(ConsoleController.class);
        consoleController.showMenu();

    }
}
