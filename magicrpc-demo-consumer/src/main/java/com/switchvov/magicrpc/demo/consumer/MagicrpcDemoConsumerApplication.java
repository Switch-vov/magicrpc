package com.switchvov.magicrpc.demo.consumer;

import com.switchvov.magicrpc.core.annotation.MagicConsumer;
import com.switchvov.magicrpc.core.consumer.ConsumerConfig;
import com.switchvov.magicrpc.demo.api.OrderService;
import com.switchvov.magicrpc.demo.api.User;
import com.switchvov.magicrpc.demo.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author switch
 * @since 2024/3/10
 */
@SpringBootApplication
@Import({ConsumerConfig.class})
@RestController
@Slf4j
public class MagicrpcDemoConsumerApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(MagicrpcDemoConsumerApplication.class);
    @MagicConsumer
    private UserService userService;
    @MagicConsumer
    private OrderService orderService;

    @RequestMapping("/")
    public User findBy(@RequestParam("id") int id) {
        return userService.findById(id);
    }

    public static void main(String[] args) {
        SpringApplication.run(MagicrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return x -> {
            log.info("userService.getId(10) = {}", userService.getId(10));
            log.info("userService.getId(10f) = {}", userService.getId(10f));
            log.info("userService.getId(User) = {}", userService.getId(new User(100, "ss")));
            log.info("userService.findById(1) = {}", userService.findById(1));
            log.info("userService.findById(1, ss) = {}", userService.findById(1, "ss"));
            log.info("orderService.findById(2) = {}", orderService.findById(2));
            log.info("userService.getName() = {}", userService.getName());
            log.info("userService.toString() = {}", userService.toString());
            log.info("userService.getLongIds() = {}", userService.getLongIds());
            log.info("userService.getIds()", userService.getIds());
            log.info("userService.getIds(new int[]{4, 5, 6}) = {}", userService.getIds(new int[]{4, 5, 6}));
            log.info("userService.getList(List) = {}", userService.getList(List.of(new User(1, "ss"), new User(2, "ss"))));
            log.info("userService.getMap(Map) = {}", userService.getMap(Map.of("A200", new User(200, "ss200"), "A300", new User(300, "ss300"))));
            log.info("userService.getFlag(boolean) = {}", userService.getFlag(false));
            log.info("userService.findUsers(User[]) = {}", List.of(userService.findUsers(new User[]{new User(3, "ss"), new User(4, "ss")})));

//            Order order404 = orderService.findById(404);
//            log.info("order:{}", order404);
        };
    }
}
