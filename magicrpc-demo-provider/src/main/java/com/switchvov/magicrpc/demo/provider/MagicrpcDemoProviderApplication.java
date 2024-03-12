package com.switchvov.magicrpc.demo.provider;

import com.switchvov.magicrpc.core.annotation.MagicProvider;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.provider.ProviderBootstrap;
import com.switchvov.magicrpc.core.provider.ProviderConfig;
import com.switchvov.magicrpc.register.client.annotation.RegisterClient;
import com.switchvov.magicrpc.register.client.annotation.RegisterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import({ProviderConfig.class, RegisterConfig.class})
@RegisterClient(registerAnnotationsOnClass = {MagicProvider.class})
public class MagicrpcDemoProviderApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(MagicrpcDemoProviderApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MagicrpcDemoProviderApplication.class, args);
    }

    @Autowired
    private ProviderBootstrap providerBootstrap;

    /**
     * 使用HTTP + JSON 来实现序列化和通信
     *
     * @param request
     * @return
     */
    @RequestMapping("/")
    public RpcResponse<?> invoke(@RequestBody RpcRequest request) {
        return providerBootstrap.invokeRequest(request);
    }

    @Bean
    public ApplicationRunner providerRun() {
        return x -> {
            RpcRequest request = new RpcRequest();
            request.setService("com.switchvov.magicrpc.demo.api.UserService");
            request.setMethod("findById@int");
            request.setArgs(new Object[]{100});

            RpcResponse<?> response = providerBootstrap.invokeRequest(request);
            LOGGER.info("return:" + response.getData());
        };
    }
}
