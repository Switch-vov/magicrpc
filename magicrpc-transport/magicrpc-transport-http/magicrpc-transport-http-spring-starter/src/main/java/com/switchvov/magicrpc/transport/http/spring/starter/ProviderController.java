package com.switchvov.magicrpc.transport.http.spring.starter;

import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.provider.ProviderInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author switch
 * @since 2024/3/23
 */
@RestController
public class ProviderController {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProviderController.class);

    private final ProviderInvoker providerInvoker;

    public ProviderController(ProviderInvoker providerInvoker) {
        this.providerInvoker = providerInvoker;
    }

    /**
     * 使用HTTP + JSON 来实现序列化和通信
     *
     * @param request
     * @return
     */
    @RequestMapping("${magicrpc.transport.http.spring.context:/}")
    public RpcResponse<?> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }

}
