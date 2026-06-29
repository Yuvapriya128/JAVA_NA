package org.example.springsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity

public class SpringConfig {
    //    automatic login -> adding user to .properties file
//    -> creating in memory


    /*
    * to get user details from a data source, create cusomt UserDetailsService
    *
    * */
//    @Autowired
//    private PasswordEncoder passwordEncoder;

//    @Bean
//    public UserDetailsService detailsService(){
//        System.err.println("UserDetailsService");
//        System.err.println("Encrypted password:"+passwordEncoder.encode("123"));
//
//        UserDetails user1= User.builder()
//                .username("user")
//                .password(passwordEncoder.encode("123"))
//                .roles("USER")
//                .build();
//
//        UserDetails user2=User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("123"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1,user2);
//    }


//    authorization: but once login it goes to ever
//    forms are not needed in postman
//    use httpBasic + csrf disable

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return
                http
                        .csrf(csrf->csrf.disable())
                        .authorizeHttpRequests(
                                auth->
                                        auth.requestMatchers("/api/security").permitAll()
                                                .anyRequest().authenticated())
//                        .formLogin(Customizer.withDefaults())
//                        .logout(Customizer.withDefaults())
                        .httpBasic(Customizer.withDefaults())
                        .build();
    }

}
