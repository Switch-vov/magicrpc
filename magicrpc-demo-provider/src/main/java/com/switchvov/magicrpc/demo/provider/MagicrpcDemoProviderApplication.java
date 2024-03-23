package com.switchvov.magicrpc.demo.provider;

import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.provider.ProviderConfig;
import com.switchvov.magicrpc.core.provider.ProviderInvoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ProviderConfig.class})
public class MagicrpcDemoProviderApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(MagicrpcDemoProviderApplication.class);

    @Autowired
    private ProviderInvoker providerInvoker;

    public static void main(String[] args) {
        SpringApplication.run(MagicrpcDemoProviderApplication.class, args);
    }

    @Bean
    public ApplicationRunner providerRun() {
        return x -> {
            RpcRequest request = RpcRequest.builder()
                    .service("com.switchvov.magicrpc.demo.api.UserService")
                    .methodSign("findById@1_int")
                    .args(new Object[]{100})
                    .build();

            RpcResponse<?> response = providerInvoker.invoke(request);
            LOGGER.info("return:" + response.getData());
        };
    }
}
