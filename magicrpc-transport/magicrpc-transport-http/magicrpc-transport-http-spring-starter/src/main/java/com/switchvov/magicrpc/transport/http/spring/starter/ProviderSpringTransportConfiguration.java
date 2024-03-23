package com.switchvov.magicrpc.transport.http.spring.starter;

import com.switchvov.magicrpc.core.provider.ProviderInvoker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author switch
 * @since 2024/3/23
 */
@Configuration
@EnableConfigurationProperties(ProviderSpringTransportProperties.class)
public class ProviderSpringTransportConfiguration {
    @Bean
    @ConditionalOnProperty(value = "magicrpc.transport.http.spring.enable", havingValue = "true", matchIfMissing = true)
    public ProviderController providerController(
            ProviderInvoker providerInvoker
    ) {
        return new ProviderController(providerInvoker);
    }
}
