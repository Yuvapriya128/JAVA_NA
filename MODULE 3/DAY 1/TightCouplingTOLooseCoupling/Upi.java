package MODULE3.TightCouplingTOLooseCoupling;

public class Upi implements PaymentService{
    @Override
    public void pay(double amt){
        System.out.println("Amount paid using upi: "+amt);
    }
}
