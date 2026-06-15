package MODULE3.TightCouplingTOLooseCoupling;

import java.util.Scanner;

public class MainDebitSetter {
    public static void main(String[] args) {
//        This is for tight coupling
//        ExpenseManager expenseManager=new ExpenseManager();
//        Loose coupling  -> setter
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter 1.credit 2.debit 3.upi 4.exit");
        int option=sc.nextInt();

        ExpenseManagerSetter expenseManager=new ExpenseManagerSetter();
        switch (option){
            case 1->{ expenseManager.setPs(new CreditCard());}
            case 2->{ expenseManager.setPs(new DebitCard());}
            case 3->{ expenseManager.setPs(new Upi());}
            case 4->{return;}
        }

        System.out.println("Enter 1.whatsapp 2.email 3.exit");
        int option2=sc.nextInt();

        switch (option){
            case 1->{ expenseManager.setNf(new Whatsapp());}
            case 2->{ expenseManager.setNf(new Email());}

            case 3->{return;}
        }


        expenseManager.payGasBill(100);
        expenseManager.payWaterBill(200);
        expenseManager.payElectricityBill(500);
    }
}
