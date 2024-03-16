package com.switchvov.magicrpc.demo.consumer;

import com.switchvov.magicrpc.core.annotation.MagicConsumer;
import com.switchvov.magicrpc.core.consumer.ConsumerConfig;
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

import java.util.List;
import java.util.Map;

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
            LOGGER.info("userService.getId(10) = {}", userService.getId(10));
            LOGGER.info("userService.getId(10f) = {}", userService.getId(10f));
            LOGGER.info("userService.getId(User) = {}", userService.getId(new User(100, "ss")));
            LOGGER.info("userService.findById(1) = {}", userService.findById(1));
            LOGGER.info("userService.findById(1, ss) = {}", userService.findById(1, "ss"));
            LOGGER.info("orderService.findById(2) = {}", orderService.findById(2));
            LOGGER.info("userService.getName() = {}", userService.getName());
            LOGGER.info("userService.toString() = {}", userService.toString());
            LOGGER.info("userService.getLongIds() = {}", userService.getLongIds());
            LOGGER.info("userService.getIds()", userService.getIds());
            LOGGER.info("userService.getIds(new int[]{4, 5, 6}) = {}", userService.getIds(new int[]{4, 5, 6}));
            LOGGER.info("userService.getList(List) = {}", userService.getList(List.of(new User(1, "ss"), new User(2, "ss"))));
            LOGGER.info("userService.getMap(Map) = {}", userService.getMap(Map.of("A200", new User(200, "ss200"))));
            LOGGER.info("userService.getFlag(boolean) = {}", userService.getFlag(false));
//            Order order404 = orderService.findById(404);
//            LOGGER.info("order:{}", order404);
        };
    }
}
