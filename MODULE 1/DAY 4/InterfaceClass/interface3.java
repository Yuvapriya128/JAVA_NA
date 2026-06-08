package InterfaceClass;

import java.util.Scanner;

interface Payment{
    void payamt(double amt);
}


public class interface3 {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter amount:");
        double amt=sc.nextDouble();
        Payment p1=new upi();
        p1.payamt(amt);

        Payment p2=new debit();
        p2.payamt(amt);

        Payment p3=new credit();
        p3.payamt(amt);


    }
}
class upi implements Payment{
   public void payamt(double amt){
       System.out.println("upi: "+amt);
    }
}
class credit implements Payment{
    public void payamt(double amt){
        System.out.println("credit: "+amt);
    }
}
class debit implements Payment{
    public void payamt(double amt){
        System.out.println("debit: "+amt);
    }
}
