package com.switchvov.magicrpc.demo.consumer;

import com.switchvov.magicrpc.core.annotation.MagicConsumer;
import com.switchvov.magicrpc.core.consumer.ConsumerConfig;
import com.switchvov.magicrpc.demo.api.Order;
import com.switchvov.magicrpc.demo.api.OrderService;
import com.switchvov.magicrpc.demo.api.User;
import com.switchvov.magicrpc.demo.api.UserService;
import com.switchvov.magicrpc.register.client.annotation.RegisterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author switch
 * @since 2024/3/10
 */
@SpringBootApplication
@Import({ConsumerConfig.class, RegisterConfig.class})
public class MagicrpcDemoConsumerApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(MagicrpcDemoConsumerApplication.class);
    @MagicConsumer
    private UserService userService;
    @MagicConsumer
    private OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(MagicrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
            User user = userService.findById(1);
            LOGGER.info("user:findById(int):{}", user);
            Order order = orderService.findById(2);
            LOGGER.info("order:findById(int):{}", order);
            int id = userService.getId(3);
            LOGGER.info("user:getId(int):{}", id);
            String name = userService.getName();
            LOGGER.info("user:getName:{}", name);
            LOGGER.info("user:toString:{}", userService.toString());
            user = userService.findById(4, "sss");
            LOGGER.info("user:findById(int,String):{}", user);
            id = userService.getId(5L);
            LOGGER.info("user:getId(long):{}", id);
            id = userService.getId(new User(6, "sss"));
            LOGGER.info("user:getId(User):{}", id);
//            Order order404 = orderService.findById(404);
//            LOGGER.info("order:{}", order404);
        };
    }
}
