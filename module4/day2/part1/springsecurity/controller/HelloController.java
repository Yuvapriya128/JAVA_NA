package org.example.springsecurity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/security")
public class HelloController {

    @GetMapping
    public String hello(){
        return "Hello world!";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String hellouser(){
        return "Hello user: search products, place order!";
    }
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String helloadmin(){
        return "Hello admin: modify customer, products";
    }

    @GetMapping("/loanOfficer")
    public String hellooOfficer(){
        return "Hello Officer: hi";
    }
    @GetMapping("/bye")
    public String bye(){
        return "Bye bye";
    }
}
