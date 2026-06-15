package MODULE3.TightCouplingTOLooseCoupling;

public class Email implements Notification{
    @Override
    public void sendMessage(String message) {
        System.out.println("Email: "+message);
    }
}
