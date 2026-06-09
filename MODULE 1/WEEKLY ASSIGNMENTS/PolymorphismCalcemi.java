package nbfcExamples;

import java.util.Scanner;

public class PolymorphismCalcemi {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Select Loan Type");
        System.out.println("1. Personal Loan");
        System.out.println("2. Gold Loan");
        System.out.println("3. Business Loan");

        int choice = sc.nextInt();

        System.out.print("Enter Loan Amount: ");
        double amount = sc.nextDouble();

        LoanProduct loan;

        switch (choice) {

            case 1:
                loan = new PersonalLoanProduct(amount);
                break;

            case 2:
                loan = new GoldLoanProduct(amount);
                break;

            case 3:
                loan = new BusinessLoanProduct(amount);
                break;

            default:
                System.out.println("Invalid Choice");
                sc.close();
                return;
        }

        loan.calculateEMI();

        sc.close();
    }
}

// Parent Class
class LoanProduct {

    protected double amount;
    protected double interestRate;
    protected int months;

    public LoanProduct(double amount, double interestRate, int months) {
        this.amount = amount;
        this.interestRate = interestRate;
        this.months = months;
    }

    public void calculateEMI() {

        double monthlyRate = interestRate / 12 / 100;

        double emi = (amount * monthlyRate * Math.pow(1 + monthlyRate, months))
                / (Math.pow(1 + monthlyRate, months) - 1);

        System.out.println("Loan Amount : " + amount);
        System.out.println("Interest Rate : " + interestRate + "%");
        System.out.println("Tenure : " + months + " months");
        System.out.printf("EMI : %.2f\n", emi);
    }
}

// Child Class 1
class PersonalLoanProduct extends LoanProduct {

    public PersonalLoanProduct(double amount) {
        super(amount, 12, 24);
    }
}

// Child Class 2
class GoldLoanProduct extends LoanProduct {

    public GoldLoanProduct(double amount) {
        super(amount, 8, 12);
    }
}

// Child Class 3
class BusinessLoanProduct extends LoanProduct {

    public BusinessLoanProduct(double amount) {
        super(amount, 15, 36);
    }
}