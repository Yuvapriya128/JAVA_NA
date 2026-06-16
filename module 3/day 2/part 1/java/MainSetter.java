import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

public class MainSetter {
    private static Scanner sc=new Scanner(System.in);
    public static void main(String[] args) {

        System.out.println("Setter injection with java Spring Configuration");
        ApplicationContext context=new AnnotationConfigApplicationContext("MySpringConfiguration.class");

        PaymentService paymentService;
        NotificationService notificationService;

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

        ExpenseManagerSetter expenseManagerSetter=new ExpenseManagerSetter();
        expenseManagerSetter.setPaymentService(paymentService);
        expenseManagerSetter.setNotificationService(notificationService);
        expenseManagerSetter.payGasBill(1400);
        expenseManagerSetter.payHouseRent(150000);
        expenseManagerSetter.payWaterBill(6000);
    }
}
