package com.switchvov.magicrpc.demo.consumer;

import com.switchvov.magicrpc.core.annotation.MagicConsumer;
import com.switchvov.magicrpc.demo.api.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MagicDemoConsumerApplication {

    @MagicConsumer
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(MagicDemoConsumerApplication.class, args);
    }

}
