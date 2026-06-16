package carSIOC;

import org.springframework.stereotype.Component;

@Component("sony")
public class SonyMusic implements MusicSystem {

    @Override
    public void digital() {
        System.out.println("Sony Digital Music System Activated");
    }
}