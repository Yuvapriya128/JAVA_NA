package org.example.springdatajpademo.Ecommerce.security;

import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.UserRole;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final CustomerRepo customerRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(CustomerRepo customerRepo, PasswordEncoder passwordEncoder) {
        this.customerRepo = customerRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (customerRepo.count() > 0) {
            return;
        }

        Customer admin = new Customer();
        admin.setName("System Admin");
        admin.setEmail("admin");
        admin.setAddress("System");
        admin.setPassword(passwordEncoder.encode("123"));
        admin.setRole(UserRole.ADMIN);

        customerRepo.save(admin);
    }
}

