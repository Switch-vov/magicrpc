package com.switchvov.magicrpc.demo.consumer;

import com.switchvov.magicrpc.register.client.annotation.RegisterClient;
import com.switchvov.magicrpc.register.client.annotation.RegisterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@RegisterClient
@Import({RegisterConfig.class})
public class MagicrpcDemoConsumerApplication {
    @Autowired
    private ConsumerService consumerService;

    public static void main(String[] args) {
        SpringApplication.run(MagicrpcDemoConsumerApplication.class, args);
    }


    @Bean
    public ApplicationRunner providerRun() {
        return x -> {
            consumerService.getUser(100);
            consumerService.getOrder(1000);
        };
    }
}
