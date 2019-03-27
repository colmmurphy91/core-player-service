package com.cluborg.coreplayerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class CorePlayerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CorePlayerServiceApplication.class, args);
    }
}
