package com.northernArc.bookmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class BookConfig {
    @Bean
    public Scanner create(){
        return new Scanner(System.in);
    }
}
