package demoSIOC;

import org.springframework.stereotype.Component;

@Component("email")
public class Email implements NotificationService{
    @Override
    public void sendmsg(String msg){
        System.out.println("Email: "+msg);
    }
}
