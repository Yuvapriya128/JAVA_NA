public class DebitCard implements PaymentService{
    @Override
    public void pay(double amt){
        System.out.println("DebitCard: "+amt+" is paid");
    }
}
