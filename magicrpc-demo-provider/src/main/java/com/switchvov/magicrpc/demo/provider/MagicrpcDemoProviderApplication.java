package com.switchvov.magicrpc.demo.provider;

import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.provider.ProviderConfig;
import com.switchvov.magicrpc.core.provider.ProviderInvoker;
import lombok.extern.slf4j.Slf4j;
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
@Import({ProviderConfig.class})
@Slf4j
public class MagicrpcDemoProviderApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(MagicrpcDemoProviderApplication.class);

    @Autowired
    private ProviderInvoker providerInvoker;

    public static void main(String[] args) {
        SpringApplication.run(MagicrpcDemoProviderApplication.class, args);
    }

    /**
     * 使用HTTP + JSON 来实现序列化和通信
     *
     * @param request
     * @return
     */
    @RequestMapping("/")
    public RpcResponse<?> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
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
            log.info("return:" + response.getData());
        };
    }
}
