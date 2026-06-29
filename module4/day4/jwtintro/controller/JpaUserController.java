package org.example.jwtintro.controller;

import org.example.jwtintro.dto.JwtRequestDTO;
import org.example.jwtintro.dto.JwtResponseDTO;
import org.example.jwtintro.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;


//encapsulation of login using DTO
//use both Autowired, constructor injection


@RestController
@RequestMapping

public class JpaUserController {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public JwtResponseDTO login(@RequestBody JwtRequestDTO jwtRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        jwtRequest.getUsername(),
                        jwtRequest.getPassword()
                )
        );
        JwtResponseDTO jwtResponse =new JwtResponseDTO();
        jwtResponse.setToken(
                jwtUtil.generateToken(jwtRequest.getUsername())
        );
        return jwtResponse;
    }


    @GetMapping("/hello")
    public String hello(){
        return "HELLO WORLD";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String user(){
        return "HELLO USER";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin(){
        return "HELLO ADMIN";
    }
}
