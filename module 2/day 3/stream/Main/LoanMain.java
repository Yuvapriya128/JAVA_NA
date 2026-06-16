package stream.Main;

import stream.connectorui.LoanImpl;
import stream.entity.Loan;

import java.util.Scanner;
import java.util.Set;

public class LoanMain {
    public static void main(String[] args) {

        LoanImpl loanSet = new LoanImpl();

        loanSet.save(new Loan(1, "vehicle loan", 200000, "accepted", 10, 12));

        loanSet.save(new Loan(5, "gold loan", 50000, "rejected", 3, 9));

        loanSet.save(new Loan(6, "business loan", 1000000, "pending", 20, 14));

        loanSet.save(new Loan(7, "agriculture loan", 300000, "accepted", 12, 11));

        loanSet.save(new Loan(8, "vehicle loan", 150000, "rejected", 2, 16));
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Display All Loans");
        System.out.println("2. Find Loans By Status");
        System.out.println("3. Find Loans By Type");
        System.out.println("4. Remove Loan");
        System.out.println("5. Add interest rate");
        System.out.println("6. update interest of x Loan:");
        System.out.println("7. sort by amt");
        System.out.println("8. sort bt amt, interest");
        System.out.println("9. Details of max, min, avg");
        System.out.print("Enter choice: ");


        do {
            int n = sc.nextInt();
            sc.nextLine();

            switch (n) {

                case 1:
                    for (Loan loan : loanSet.findAll()) {
                        System.out.println(loan);
                    }
                    break;

                case 2:
                    System.out.print("Enter status: ");
                    String status = sc.nextLine();

                    for (Loan loan : loanSet.findByStatus(status)) {
                        System.out.println(loan);
                    }
                    break;

                case 3:
                    System.out.print("Enter loan type: ");
                    String type = sc.nextLine();

                    for (Loan loan : loanSet.findByType(type)) {
                        System.out.println(loan);
                    }
                    break;

                case 4:
                    System.out.print("Enter loan id to remove: ");
                    int id = sc.nextInt();

                    loanSet.remove(id);
                    System.out.println("Loan removed successfully");

                    break;
                case 5:
                    System.out.println("Enter interest rate:");
                    int rate=sc.nextInt();

                    loanSet.updateInterest(rate);
                    System.out.println("Added successfully");
                    break;
                case 6:
                    System.out.println("Enter loan type:");
                    String intype=sc.nextLine();
                    System.out.println("Enter interest rate:");
                    int irate=sc.nextInt();

                    for(Loan l:loanSet.updateLoanInterest(intype,irate)){
                        System.out.println(l);
                    }
                    System.out.println("Added successfully");
                    break;

                case 7:


                    for(Loan l:loanSet.sortLoanByAmount()){
                        System.out.println(l);
                    }
                    System.out.println("sorted successfully");
                    break;

                case 8:


                    for(Loan l:loanSet.sortLoanByAmtInt()){
                        System.out.println(l);
                    }
                    System.out.println("Added successfully");
                    break;
                case 9:
                    loanSet.details();
                    break;

                default:
                    System.out.println("Invalid choice");
                    return;
            }
        }
        while(true);
    }
}