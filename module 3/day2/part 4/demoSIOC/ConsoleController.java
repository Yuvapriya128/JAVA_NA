package demoSIOC;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleController {
    private final Scanner sc;
    private final ExpenseManager expenseManager;

    public ConsoleController(Scanner sc, ExpenseManager expenseManager) {
        this.sc=sc;
        this.expenseManager=expenseManager;
    }

    public void showMenu(){
        System.out.println("This is by console controller");
        System.out.println("1.house rent");
        System.out.println("2.Water Bill");
        System.out.println("3.Gas Bill");

        do{
            System.out.println("Enter option:");
            int option=sc.nextInt();
            redirectoption(option);
        }while(true);

    }
    private void redirectoption(int option){
        switch (option){
            case 1->{
                System.out.println("Enter amt:");
                double amt=sc.nextDouble();
                expenseManager.payGasBill(amt);
            }
            case 2->{
                System.out.println("Enter amt:");
                double amt=sc.nextDouble();
                expenseManager.payWaterBill(amt);
            }
            case 3->{
                System.out.println("Enter amt:");
                double amt=sc.nextDouble();
                expenseManager.payHouseRent(amt);
            }

            default -> {throw new IllegalArgumentException("Invalid choice");}
        }
    }
}
