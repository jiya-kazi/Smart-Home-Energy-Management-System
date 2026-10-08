package com.role.implementation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoleBasedAuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoleBasedAuthenticationApplication.class, args);
    }
}
