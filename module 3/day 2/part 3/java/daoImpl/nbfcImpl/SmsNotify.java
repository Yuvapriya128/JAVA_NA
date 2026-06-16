package daoImpl.nbfcImpl;

import dao.nbfc.NotificationService;

public class SmsNotify implements NotificationService {

    @Override
    public void sendMessage(String message) {
        System.out.println("SMS: "+message);
    }
}
