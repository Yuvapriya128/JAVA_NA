package demoSIOC;

import org.springframework.stereotype.Component;

@Component("credit")
public class CreditCard implements PaymentService{
    @Override
    public void pay(double amt){
        System.out.println("CreditCard: "+amt+" is paid");
    }
}
