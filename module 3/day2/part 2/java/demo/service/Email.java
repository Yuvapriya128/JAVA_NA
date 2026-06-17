package demo.service;

public class Email implements NotificationService{
    @Override
    public void sendmsg(String msg){
        System.out.println("Email: "+msg);
    }
}
