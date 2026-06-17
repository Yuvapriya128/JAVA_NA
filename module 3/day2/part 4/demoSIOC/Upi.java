package demoSIOC;

import org.springframework.stereotype.Component;

@Component("upi")
public class Upi implements PaymentService{
    @Override
    public void pay(double amt){
        System.out.println("UPI: "+amt+" is paid");
    }

}
