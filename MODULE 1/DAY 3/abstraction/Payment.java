import java.util.*;

public abstract class Payment{
    public abstract void pay(double amt);
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter amount:");
        double amt=sc.nextDouble();
        System.out.println("Enter 1,2,3:");
        int n=sc.nextInt();
        Payment pm;
        switch (n) {
            case 1 :
                pm=new upi();
                break;
            case 2:
                pm=new debit();
                break;
            case 3:
                pm=new credit();
                break;
            default:
                System.out.println("error");
                return;
        }
        pm.pay(amt);
    }
}
class upi extends Payment{
     public void pay(double amt){
        System.out.println("Amount "+amt+"is paid through "+"upi");
    }

}
class debit extends Payment{
     public void pay(double amt){
        System.out.println("Amount "+amt+"is paid through "+"debit");
    }
}
class credit extends Payment{
    public void pay(double amt){
        System.out.println("Amount "+amt+"is paid through "+"credit");
    }
}