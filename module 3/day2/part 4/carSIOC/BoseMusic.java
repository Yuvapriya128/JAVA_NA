package carSIOC;

import org.springframework.stereotype.Component;

@Component("bose")
public class BoseMusic implements MusicSystem {

    @Override
    public void digital() {
        System.out.println("Bose Premium Digital Music System Activated");
    }
}