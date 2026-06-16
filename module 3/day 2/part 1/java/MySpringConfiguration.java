import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;



/*
* Configuration is like Factory
* */
@Configuration
public class MySpringConfiguration {
    private static Scanner sc=new Scanner(System.in);

//    Not ideal way
//    @Bean
//    public PaymentService getPaymentService(){
//        System.out.println("Enter 1.debit 2.credit 3.upi");
//        int option=sc.nextInt();
//        switch (option){
//            case 1->{return new DebitCard();}
//            case 2->{return new CreditCard();}
//            case 3->{return new Upi();}
//
//        }
//        return null;
//    }
//    @Bean
//    public NotificationService getNotificationService(){
//        System.out.println("Enter 1.Email 2.Whatsapp");
//        int option=sc.nextInt();
//        switch (option){
//            case 1->{return new Email();}
//            case 2->{return new Whatsapp();}
//        }
//        return null;
//    }

    @Bean("debit")
    public PaymentService getPayment1(){
        return new DebitCard();
    }


    @Bean("credit")
    public PaymentService getPayment2(){
        return new CreditCard();
    }
    @Bean("upi")
    public PaymentService getPayment3(){
        return new Upi();
    }

    @Bean("email")
    public NotificationService getNotification1(){
        return new Email();
    }
    @Bean("whatsapp")
    public NotificationService getNotification2(){
        return new Whatsapp();
    }
    /* For using Qualifiers
    @Bean
    public ExpenseManager getExpenseManager(@Qualifier("upi") PaymentService ps,@Qualifier("email") NotificationService nf){
        return new ExpenseManager(ps,nf);
    }
    */

}
