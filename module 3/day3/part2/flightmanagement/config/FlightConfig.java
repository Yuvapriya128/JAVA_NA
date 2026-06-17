package com.northernArc.flightmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class FlightConfig {
    @Bean
    public Scanner sc(){
        return new Scanner(System.in);
    }
}
