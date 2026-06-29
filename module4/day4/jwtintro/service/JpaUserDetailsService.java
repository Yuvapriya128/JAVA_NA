package org.example.jwtintro.service;

import org.example.jwtintro.model.JpaUser;
import org.example.jwtintro.repo.JpaUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {
    @Autowired
    public PasswordEncoder passwordEncoder;
    @Autowired
    public JpaUserRepo jpaUserRepo;

//    do not encode in loadUserByUsername
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        JpaUser jpaUser=jpaUserRepo.findByUsername(username);
        return User.builder()
                .username(jpaUser.getUsername())
                .password(jpaUser.getPassword())
                .roles(jpaUser.getRole())
                .build();

    }
}
