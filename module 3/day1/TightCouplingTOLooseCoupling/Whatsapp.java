package MODULE3.TightCouplingTOLooseCoupling;

public class Whatsapp implements Notification{
    @Override
    public void sendMessage(String message) {
        System.out.println("WhatsApp: "+message);
    }
}
