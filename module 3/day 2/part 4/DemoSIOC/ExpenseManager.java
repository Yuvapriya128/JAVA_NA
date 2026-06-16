package DemoSIOC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class ExpenseManager {
    @Autowired
    @Qualifier("debit")
    private PaymentService paymentService;

    @Autowired
    @Qualifier("email")
    private NotificationService notificationService;


    public void payHouseRent(double amt){
        System.out.println("Paying House Rent");
        paymentService.pay(amt);
        notificationService.sendmsg("Paid House Rent");
    }
    public void payWaterBill(double amt){
        System.out.println("Paying Water bill");
        paymentService.pay(amt);
        notificationService.sendmsg("Paid Water bill");
    }
    public void payGasBill(double amt){
        System.out.println("Paying Gas Bill");
        paymentService.pay(amt);
        notificationService.sendmsg("Paid Gas Bill");
    }
}
