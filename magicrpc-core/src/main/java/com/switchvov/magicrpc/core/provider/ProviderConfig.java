package com.switchvov.magicrpc.core.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author switch
 * @since 2024/3/7
 */
@Configuration
public class ProviderConfig {
    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }
}
