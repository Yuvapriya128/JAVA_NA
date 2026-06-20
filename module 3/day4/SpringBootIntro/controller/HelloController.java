package com.demo.springbootdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(){
        return ("Hell World");
    }

    @RequestMapping("")
    public String welcome(){
        return "Welcome";
    }

    @RequestMapping("/exit")
    public String exit(){
        return "Bye bye";
    }


}
