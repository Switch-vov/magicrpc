package com.switchvov.magicrpc.register.client.annotation;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * @author switch
 * @since 2024/3/7
 */
@Configuration
@EnableConfigurationProperties
public class RegisterConfig {
    @Bean
    public ApplicationRunner registerRunner(
            RegisterProperties registerProperties,
            ApplicationContext applicationContext,
            RestClient restClient
    ) {
        return x -> {
            RegisterClientHandler handler = new RegisterClientHandler(registerProperties,
                    applicationContext, restClient);
            handler.register();
        };
    }

    @Bean
    public RegisterCli registerCli(
            RegisterProperties registerProperties,
            RestClient restClient
    ) {
        return new RegisterCli(registerProperties, restClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public RegisterProperties zookeeperProperties() {
        return new RegisterProperties();
    }

    @Bean
    public RestClient registerRestClient() {
        return RestClient.builder().build();
    }
}
