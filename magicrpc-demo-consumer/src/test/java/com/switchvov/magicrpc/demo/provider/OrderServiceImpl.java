package com.switchvov.magicrpc.demo.provider;

import com.switchvov.magicrpc.core.annotation.MagicProvider;
import com.switchvov.magicrpc.core.api.RpcException;
import com.switchvov.magicrpc.demo.api.Order;
import com.switchvov.magicrpc.demo.api.OrderService;
import org.springframework.stereotype.Service;

/**
 * @author switch
 * @since 2024/3/7
 */
@Service
@MagicProvider
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {
        if (id == 404) {
            throw new RpcException("404 exception");
        }

        return new Order(id.longValue(), 111.11f);
    }
}
