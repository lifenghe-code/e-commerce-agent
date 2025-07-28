package com.ecommerce.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ECommerceAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ECommerceAgentApplication.class, args);
    }
}
