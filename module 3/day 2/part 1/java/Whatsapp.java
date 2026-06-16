public class Whatsapp implements NotificationService{
    @Override
    public void sendmsg(String msg){
        System.out.println("Whatsapp: "+msg);
    }
}
