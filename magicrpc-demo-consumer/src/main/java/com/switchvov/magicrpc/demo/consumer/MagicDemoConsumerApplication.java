package com.switchvov.magicrpc.demo.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MagicDemoConsumerApplication {
    @Autowired
    private ConsumerService consumerService;

    public static void main(String[] args) {
        SpringApplication.run(MagicDemoConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner providerRun() {
        return x -> {
            consumerService.getUser(100);
            consumerService.getOrder(1000);
        };
    }
}
