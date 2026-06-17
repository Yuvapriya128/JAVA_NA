package MODULE3.TightCouplingTOLooseCoupling;

public class DebitCard implements PaymentService {

    @Override
    public void pay(double amt){
        System.out.println("Amount paid using debitcard: "+amt);
    }

}
