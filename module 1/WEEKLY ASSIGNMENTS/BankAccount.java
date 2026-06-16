package covariant;

import java.util.Scanner;

public class BankAccount {
    protected String Accowner;
    protected double balance;
    public BankAccount(String Accowner,double balance){
        this.Accowner=Accowner;
        this.balance=balance;
    }
    public void deposit(double amt){
        balance+=amt;
    }
    public void withdraw (double amt){
        if(amt>balance)
        System.out.println("Balance is insufficient");
        else{
            balance-=amt;
            System.out.println(amt+" is withdrawn");
        }
    }
    public void checkbal(){
        System.out.println("Remaining balance: "+balance);
    }

    public static void main(String[] args) {
        System.out.println("Enter Account owner name:");
        Scanner sc=new Scanner(System.in);
        String owner=sc.nextLine();

        System.out.println("Enter initial balance");
        double iamt=sc.nextDouble();

        BankAccount ba=new BankAccount(owner,iamt);

        do{
            System.out.println("Enter 1(Deposit),2(Withdraw),3(check),4(exit)");
            int option=sc.nextInt();
            switch (option){
                case 1:
                    System.out.println("Enter amount to deposit:");
                    double dep=sc.nextDouble();
                    ba.deposit(dep);
                    break;
                case 2:
                    System.out.println("Enter amount to withdraw:");
                    double with=sc.nextDouble();
                    ba.withdraw(with);
                    break;
                case 3:
                    ba.checkbal();
                    break;
                case 4:
                    return;
            }

        }while(true);
    }
}
