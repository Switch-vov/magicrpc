package com.switchvov.magicrpc.register.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.zookeeper.ZookeeperAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ZookeeperAutoConfiguration.class)
public class MagicrpcDemoRegisterApplication {
    public static void main(String[] args) {
        SpringApplication.run(MagicrpcDemoRegisterApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
