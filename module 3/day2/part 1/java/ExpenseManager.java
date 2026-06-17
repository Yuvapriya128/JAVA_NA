public class ExpenseManager {
    private PaymentService paymentService;
    private NotificationService notificationService;

    public ExpenseManager(PaymentService paymentService, NotificationService notificationService) {
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }
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
