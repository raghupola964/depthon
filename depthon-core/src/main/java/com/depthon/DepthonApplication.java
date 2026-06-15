package com.depthon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DepthonApplication {

    public static void main(String[] args) {
        SpringApplication.run(DepthonApplication.class, args);
    }
}