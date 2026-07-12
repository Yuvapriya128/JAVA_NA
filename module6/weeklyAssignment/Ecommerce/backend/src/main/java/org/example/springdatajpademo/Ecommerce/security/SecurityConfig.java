package org.example.springdatajpademo.Ecommerce.security;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.example.springdatajpademo.Ecommerce.model.Customer;
import org.example.springdatajpademo.Ecommerce.model.UserRole;
import org.example.springdatajpademo.Ecommerce.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomerRepo customerRepo;

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("Initializing BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        logger.debug("Configuring DaoAuthenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false); // Allow UserDetailsService exceptions to propagate
        logger.debug("DaoAuthenticationProvider configured with UserDetailsService and PasswordEncoder");
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        logger.debug("Initializing UserDetailsService");
        return email -> {
            logger.debug("Loading user details for email: {}", email);
            Customer customer = customerRepo.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.warn("Customer not found with email: {}", email);
                        return new UsernameNotFoundException("Customer not found with email: " + email);
                    });

            logger.debug("Customer found: {}. Email: {}, Role: {}", customer.getId(), customer.getEmail(), customer.getRole());
            logger.debug("Password hash from database (first 20 chars): {}...",
                    customer.getPassword() != null ? customer.getPassword().substring(0, Math.min(20, customer.getPassword().length())) : "null");

            String roleName = customer.getRole() != null
                    ? customer.getRole().name()
                    : UserRole.USER.name();

            logger.debug("===== USER DETAILS SERVICE ======");
            logger.debug("Customer email: {}", customer.getEmail());
            logger.debug("Customer role from DB: {}", customer.getRole());
            logger.debug("Role name to use: {}", roleName);

            var userDetails = User.builder()
                    .username(customer.getEmail())
                    .password(customer.getPassword())
                    .roles(roleName)
                    .build();
            
            logger.debug("UserDetails built with:");
            logger.debug("  Username: {}", userDetails.getUsername());
            logger.debug("  Authorities: {}", userDetails.getAuthorities());
            logger.debug("  Authorities count: {}", userDetails.getAuthorities().size());
            userDetails.getAuthorities().forEach(auth -> 
                logger.debug("    - Authority: {}", auth.getAuthority())
            );
            
            return userDetails;
        };
    }

    // ...existing code...

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        logger.debug("Creating AuthenticationManager from configuration");
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.debug("Configuring CORS for http://localhost:4200");
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:4200"));

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        logger.debug("Configuring security filter chain");
        logger.debug("Permit all access to: /api/ecom/auth/**, POST /api/ecom/customer");
        logger.debug("JwtFilter is added BEFORE UsernamePasswordAuthenticationFilter");

        return http
                .cors(cors -> {})
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/ecom/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/ecom/customer").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(daoAuthenticationProvider())
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}