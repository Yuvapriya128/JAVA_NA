package demo.service;

public class ExpenseManagerSetter {
    private PaymentService paymentService;
    private NotificationService notificationService;

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public NotificationService getNotificationService() {
        return notificationService;
    }

    public void setNotificationService(NotificationService notificationService) {
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

