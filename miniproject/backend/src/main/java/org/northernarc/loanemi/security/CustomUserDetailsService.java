package org.northernarc.loanemi.security;

import org.northernarc.loanemi.model.Customer;
import org.northernarc.loanemi.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final CustomerRepository customerRepository;

    public CustomUserDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user details for email={}", email);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User details not found for email={}", email);
                    return new UsernameNotFoundException("User not found");
                });

        String role = customer.getRole() == null ? "USER" : customer.getRole().name();
        log.debug("User details loaded for email={} role={}", email, role);
        return User.builder()
                .username(customer.getEmail())
                .password(customer.getPassword())
                .roles(role)
                .disabled(!customer.isActive())
                .build();
    }
}
