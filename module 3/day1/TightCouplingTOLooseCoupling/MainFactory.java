package MODULE3.TightCouplingTOLooseCoupling;

import java.util.Scanner;

public class MainFactory {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter 1.credit 2.debit 3.upi");
        int option=sc.nextInt();

        PaymentService ps= PaymentFactoryAndNotification.getPaymentService(option);

        System.out.println("Enter 1.whatsapp 2.email");
        int option2=sc.nextInt();

        Notification nf=PaymentFactoryAndNotification.getNotificationService(option2);


        ExpenseManager expenseManager=new ExpenseManager(ps,nf);
        expenseManager.payGasBill(100);
        expenseManager.payWaterBill(200);
        expenseManager.payElectricityBill(500);
    }
}
