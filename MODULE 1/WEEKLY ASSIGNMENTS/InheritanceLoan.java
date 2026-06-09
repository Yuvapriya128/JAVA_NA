package nbfcExamples;

import java.util.Scanner;

public class InheritanceLoan {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Select Loan Type:");
        System.out.println("1. Personal Loan");
        System.out.println("2. Gold Loan");
        System.out.println("3. Business Loan");

        int choice = sc.nextInt();

        System.out.print("Enter Loan Amount: ");
        double amount = sc.nextDouble();

        Loan l = null;

        switch (choice) {

            case 1:
                l = new PersonalLoan(amount);
                break;

            case 2:
                l = new GoldLoan(amount);
                break;

            case 3:
                l = new BusinessLoan(amount);
                break;

            default:
                System.out.println("Invalid Choice");
                System.exit(0);
        }

        System.out.println("Loan Amount: " + l.amount);
        l.interest();

        sc.close();
    }
}

class Loan {

    protected double amount;

    public Loan(double amount) {
        this.amount = amount;
    }

    public void interest() {
        System.out.println("General Loan Interest");
    }
}

class PersonalLoan extends Loan {

    public PersonalLoan(double amount) {
        super(amount);
    }

    @Override
    public void interest() {
        System.out.println("Personal Loan Interest: 12%");
    }
}

class GoldLoan extends Loan {

    public GoldLoan(double amount) {
        super(amount);
    }

    @Override
    public void interest() {
        System.out.println("Gold Loan Interest: 8%");
    }
}

class BusinessLoan extends Loan {

    public BusinessLoan(double amount) {
        super(amount);
    }

    @Override
    public void interest() {
        System.out.println("Business Loan Interest: 15%");
    }
}