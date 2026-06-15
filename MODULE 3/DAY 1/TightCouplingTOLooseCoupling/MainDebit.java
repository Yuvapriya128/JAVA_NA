package MODULE3.TightCouplingTOLooseCoupling;

import java.util.Scanner;

public class MainDebit {
    public static void main(String[] args) {
//        This is for tight coupling
//        ExpenseManager expenseManager=new ExpenseManager();
//        Loose coupling
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter 1.credit 2.debit 3.upi 4.exit");
        int option=sc.nextInt();
        PaymentService paymentService=null;
        switch (option){
            case 1->{ paymentService=new CreditCard();}
            case 2->{ paymentService=new DebitCard();}
            case 3->{ paymentService=new Upi();}
            case 4->{return;}
        }

        System.out.println("Enter 1.whatsapp 2.email 3.exit");
        int option2=sc.nextInt();
        Notification nf=null;
        switch (option){
            case 1->{ nf=new Whatsapp();}
            case 2->{ nf=new Email();}

            case 3->{return;}
        }

        ExpenseManager expenseManager=new ExpenseManager(paymentService,nf);
        expenseManager.payGasBill(100);
        expenseManager.payWaterBill(200);
        expenseManager.payElectricityBill(500);
    }
}
