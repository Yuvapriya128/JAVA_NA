package org.example.springsecurity.service;

import org.example.springsecurity.model.JpaUser;
import org.example.springsecurity.repo.JpaUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
public class JPAUserDetailsService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JpaUserRepo jpaUserRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        JpaUser jpaUser=jpaUserRepo.findByName(username);
        return
        User.builder().username(jpaUser.getName()).password(jpaUser.getPassword()).roles(jpaUser.getRole()).build();

    }
//RUN THIS ONLY ONCE
//    @PostConstruct
//    public void init(){
//        JpaUser jpaUser=new JpaUser();
//        jpaUser.setName("user");
//        jpaUser.setPassword("123");
//        jpaUser.setRole("USER");
//
//        JpaUser jpaUser1=new JpaUser();
//        jpaUser1.setName("admin");
//        jpaUser1.setPassword("123");
//        jpaUser1.setRole("ADMIN");
//
//        jpaUserRepo.save(jpaUser);
//        jpaUserRepo.save(jpaUser1);
//
//    }
}
