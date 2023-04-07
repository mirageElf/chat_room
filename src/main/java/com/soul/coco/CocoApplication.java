package com.soul.coco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextListener;

@SpringBootApplication
public class CocoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CocoApplication.class, args);
    }

}
