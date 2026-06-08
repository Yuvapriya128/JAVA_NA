package covariant;

import java.util.Scanner;

public class RechargeTracker {
    protected String owner;
    protected double balance;
    public RechargeTracker(String owner,double balance){
        this.owner=owner;
        this.balance=balance;
    }
    public void topup(double amt){
        balance+=amt;
    }
//    10 mins per rupee
    public void makecall (double min){
        double totalDuration=balance*5;
        if(totalDuration<min){
            System.out.println("You cannot make this call");
        }else{
            totalDuration-=min;
            balance=totalDuration/5;
            System.out.println("Your current balance:"+balance);
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

        RechargeTracker rt=new RechargeTracker(owner,iamt);

        do{
            System.out.println("Enter 1(topup),2(makecall),3(check),4(exit)");
            int option=sc.nextInt();
            switch (option){
                case 1:
                    System.out.println("Enter amount to topup:");
                    double dep=sc.nextDouble();
                    rt.topup(dep);
                    break;
                case 2:
                    System.out.println("Enter mins to call:");
                    double with=sc.nextDouble();
                    rt.makecall(with);
                    break;
                case 3:
                    rt.checkbal();
                    break;
                case 4:
                    return;
            }

        }while(true);
    }
}

