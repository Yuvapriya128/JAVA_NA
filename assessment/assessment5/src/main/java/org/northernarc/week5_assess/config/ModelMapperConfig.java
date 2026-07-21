package org.northernarc.week5_assess.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean(name = "modelMapper")
    public Object modelMapper() {
        return new Object();
    }
}

