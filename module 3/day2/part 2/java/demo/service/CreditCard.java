package demo.service;

public class CreditCard implements PaymentService{
    @Override
    public void pay(double amt){
        System.out.println("CreditCard: "+amt+" is paid");
    }
}
