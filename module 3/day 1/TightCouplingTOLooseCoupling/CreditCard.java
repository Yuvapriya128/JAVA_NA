package MODULE3.TightCouplingTOLooseCoupling;

public class CreditCard implements PaymentService{
    @Override
    public void pay(double amt){
        System.out.println("Amount paid using credit card: "+amt);
    }
}
