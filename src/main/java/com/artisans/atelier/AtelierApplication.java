package com.artisans.atelier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class AtelierApplication {

    public static void main(String[] args) {
        SpringApplication.run(AtelierApplication.class, args);
    }

}
