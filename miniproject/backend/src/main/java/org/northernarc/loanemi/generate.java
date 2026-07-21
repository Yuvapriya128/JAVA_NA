package org.northernarc.loanemi;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

 class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("Yuvapriya@1  -> " + encoder.encode("Yuvapriya@1"));
        //System.out.println("Yuvapriya@1 -> " + encoder.encode("Yuvapriya@1"));
    }
}