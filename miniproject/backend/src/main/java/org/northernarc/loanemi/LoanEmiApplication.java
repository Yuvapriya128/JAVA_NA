package org.northernarc.loanemi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LoanEmiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanEmiApplication.class, args);
    }
}
