package com.switchvov.magicrpc.demo.consumer;

import com.switchvov.magicrpc.core.annotation.MagicConsumer;
import com.switchvov.magicrpc.demo.api.Order;
import com.switchvov.magicrpc.demo.api.OrderService;
import com.switchvov.magicrpc.demo.api.User;
import com.switchvov.magicrpc.demo.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author switch
 * @since 2024/3/8
 */
@Service
public class ConsumerService {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConsumerService.class);

    @MagicConsumer
    private UserService userService;

    @MagicConsumer
    private OrderService orderService;

    public void getUser(int id) {
        User user = userService.findById(id);
        LOGGER.info("get uid: {}, user:{}", id, user);
    }

    public void getOrder(int id) {
        Order order = orderService.findById(id);
        LOGGER.info("get uid: {}, order:{}", id, order);
    }
}
