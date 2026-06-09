package nbfcExamples;

public class Composition {

    public static void main(String[] args) {

        // Creating BNPL Account
        BNPLAccount account =
                new BNPLAccount("Yuvapriya", 50000);

        account.showAccountDetails();

        account.makePurchase(12000);

        account.showAccountDetails();
    }
}

class BNPLAccount {

    private String customerName;
    private double creditLimit;

    // Composition
    private Wallet wallet;

    public BNPLAccount(String customerName,
                       double creditLimit) {

        this.customerName = customerName;
        this.creditLimit = creditLimit;

        // Wallet created inside BNPLAccount
        wallet = new Wallet();
    }

    public void makePurchase(double amount) {

        System.out.println("\nProcessing Purchase...");

        if (amount <= creditLimit) {

            wallet.pay(amount);

            creditLimit -= amount;

            System.out.println("Purchase Successful");
        }
        else {

            System.out.println("Insufficient Credit Limit");
        }
    }

    public void showAccountDetails() {

        System.out.println("\n------ BNPL ACCOUNT DETAILS ------");
        System.out.println("Customer Name : " + customerName);
        System.out.println("Available Credit : " + creditLimit);

        wallet.showWalletStatus();
    }
}

class Wallet {

    private boolean active;
    private double totalSpent;

    public Wallet() {

        active = true;
        totalSpent = 0;

        System.out.println("Wallet Activated Successfully");
    }

    public void pay(double amount) {

        if (active) {

            totalSpent += amount;

            System.out.println("Paid using Wallet : " + amount);
        }
        else {

            System.out.println("Wallet is Inactive");
        }
    }

    public void showWalletStatus() {

        System.out.println("Wallet Status : Active");
        System.out.println("Total Amount Spent : " + totalSpent);
    }
}