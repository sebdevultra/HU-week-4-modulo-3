package com.riwi.creditapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application class for Credit Application Service.
 * 
 * This application implements a credit application management system
 * using Hexagonal Architecture (Ports and Adapters).
 */
@SpringBootApplication
@EnableTransactionManagement
public class CreditApplicationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditApplicationServiceApplication.class, args);
    }
}
