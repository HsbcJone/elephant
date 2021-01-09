package com.elephant.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootScalaIntegration {

    public static String[] args;

    public static void main(String[] args) {
        SpringBootScalaIntegration.args = args;
        System.setProperty("user.timezone", "GMT+08");
        SpringApplication.run(SpringBootScalaIntegration.class, args);
    }
}