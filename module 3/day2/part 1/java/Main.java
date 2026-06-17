import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

public class Main {
    private static Scanner sc=new Scanner(System.in);
    public static void main(String[] args) {

        ApplicationContext context=new AnnotationConfigApplicationContext(MySpringConfiguration.class);

//        This works for single bean only
//        PaymentService paymentService=context.getBean(PaymentService.class);
//        NotificationService notificationService=context.getBean(NotificationService.class);

        PaymentService paymentService;
        NotificationService notificationService;

//        Or use variables to store the string and create it with that
        /*
        String str="debit";
        * paymentService=context.getBean(str,PaymentService.class);
        *
        * */

        System.out.println("Enter 1.debit 2.credit 3.upi");
        int opt=sc.nextInt();
        switch (opt){
            case 1->{paymentService=context.getBean("debit",PaymentService.class);}
            case 2->{paymentService=context.getBean("credit",PaymentService.class);}
            case 3->{paymentService=context.getBean("upi",PaymentService.class);}
            default -> {throw new IllegalArgumentException();}
        }

        System.out.println("Enter 1.Email 2.Whatsapp");
        int option=sc.nextInt();
        switch (option){
            case 1->{notificationService= context.getBean("email",NotificationService.class);}
            case 2->{notificationService= context.getBean("whatsapp",NotificationService.class);}
            default -> {throw new IllegalArgumentException();}
        }

// This is for Qualifier
//        ExpenseManager expenseManager=context.getBean(ExpenseManager.class);

        ExpenseManager expenseManager=new ExpenseManager(paymentService,notificationService);
        expenseManager.payGasBill(1400);
        expenseManager.payHouseRent(150000);
        expenseManager.payWaterBill(6000);

    }
}
