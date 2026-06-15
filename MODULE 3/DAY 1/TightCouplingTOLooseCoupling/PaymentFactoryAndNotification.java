package MODULE3.TightCouplingTOLooseCoupling;

public class PaymentFactoryAndNotification {
    private static CreditCard creditCard=new CreditCard();
    private static DebitCard debitCard=new DebitCard();
    private static Upi upi=new Upi();

    private static Whatsapp whatsapp=new Whatsapp();
    private static Email email=new Email();

    public static PaymentService getPaymentService(int n){
        switch (n){
            case 1->{return creditCard;}
            case 2->{return debitCard;}
            case 3->{return upi;}
        }
        return null;

    }

    public static Notification getNotificationService(int n){
        switch (n){
            case 1->{return whatsapp;}
            case 2->{return email;}

        }
        return null;
    }
}
