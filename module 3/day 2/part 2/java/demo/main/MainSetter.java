package demo.main;

import demo.service.ExpenseManager;
import demo.service.ExpenseManagerSetter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

public class MainSetter {
    private static Scanner sc=new Scanner(System.in);
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("ApplicationContextSetter.xml");

        System.out.println("Expense Manager Setter with ApplicationContext with Property");
        System.out.println("1.credit+email");
        System.out.println("2.credit+whatsapp");
        System.out.println("3.debit+email");
        System.out.println("4.debit+whatsapp");
        System.out.println("5.upi+email");
        System.out.println("6.upi+whatsapp");

        ExpenseManagerSetter expenseManager;

        int opt=sc.nextInt();
        switch (opt){
            case 1->{expenseManager=context.getBean("ExpenseManager1",ExpenseManagerSetter.class);}
            case 2->{expenseManager=context.getBean("ExpenseManager2",ExpenseManagerSetter.class);}
            case 3->{expenseManager=context.getBean("ExpenseManager3",ExpenseManagerSetter.class);}
            case 4->{expenseManager=context.getBean("ExpenseManager4",ExpenseManagerSetter.class);}
            case 5->{expenseManager=context.getBean("ExpenseManager5",ExpenseManagerSetter.class);}
            case 6->{expenseManager=context.getBean("ExpenseManager6",ExpenseManagerSetter.class);}
            default -> {throw new IllegalArgumentException("Invalid option");}

        }


        expenseManager.payGasBill(1400);
        expenseManager.payHouseRent(150000);
        expenseManager.payWaterBill(6000);
    }
}
