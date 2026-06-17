package daoImpl.nbfcImpl;

import dao.nbfc.NotificationService;

public class EmailNotify implements NotificationService {
    @Override
    public void sendMessage(String message) {
        System.out.println("Email: "+message);
    }
}
