package demo.main;

import demo.service.ExpenseManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;

public class Main {
    private static Scanner sc=new Scanner(System.in);
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext.xml");

        System.out.println("1.credit+email");
        System.out.println("2.credit+whatsapp");
        System.out.println("3.debit+email");
        System.out.println("4.debit+whatsapp");
        System.out.println("5.upi+email");
        System.out.println("6.upi+whatsapp");

        ExpenseManager expenseManager;

        int opt=sc.nextInt();
        switch (opt){
            case 1->{expenseManager=context.getBean("ExpenseManager1",ExpenseManager.class);}
            case 2->{expenseManager=context.getBean("ExpenseManager2",ExpenseManager.class);}
            case 3->{expenseManager=context.getBean("ExpenseManager3",ExpenseManager.class);}
            case 4->{expenseManager=context.getBean("ExpenseManager4",ExpenseManager.class);}
            case 5->{expenseManager=context.getBean("ExpenseManager5",ExpenseManager.class);}
            case 6->{expenseManager=context.getBean("ExpenseManager6",ExpenseManager.class);}
            default -> {throw new IllegalArgumentException("Invalid option");}

        }


        expenseManager.payGasBill(1400);
        expenseManager.payHouseRent(150000);
        expenseManager.payWaterBill(6000);
    }
}
