package com.switchvov.magicrpc.demo.consumer;

import com.switchvov.magicrpc.core.annotation.MagicConsumer;
import com.switchvov.magicrpc.core.consumer.ConsumerConfig;
import com.switchvov.magicrpc.demo.api.Order;
import com.switchvov.magicrpc.demo.api.OrderService;
import com.switchvov.magicrpc.demo.api.User;
import com.switchvov.magicrpc.demo.api.UserService;
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
@Import({ConsumerConfig.class})
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
            LOGGER.info("user:{}", user);
            Order order = orderService.findById(2);
            LOGGER.info("order:{}", order);
            int id = userService.getId(3);
            LOGGER.info("user:get_id:{}", id);
            String name = userService.getName();
            LOGGER.info("user:get_name:{}", name);
            LOGGER.info("user:toString:{}", userService.toString());
//            Order order404 = orderService.findById(404);
//            LOGGER.info("order:{}", order404);
        };
    }
}
