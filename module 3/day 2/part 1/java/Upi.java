public class Upi implements PaymentService{
    @Override
    public void pay(double amt){
        System.out.println("UPI: "+amt+" is paid");
    }

}
