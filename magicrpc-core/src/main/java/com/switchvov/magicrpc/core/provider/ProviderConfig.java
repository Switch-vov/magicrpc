package com.switchvov.magicrpc.core.provider;

import com.switchvov.magicrpc.core.api.RegistryCenter;
import com.switchvov.magicrpc.core.registry.zookeeper.ZKRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
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

    @Bean
    public ApplicationRunner providerBootstrapRunner(@Autowired ProviderBootstrap providerBootstrap) {
        return x -> providerBootstrap.start();
    }

    @Bean
    public ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }

    @Bean
    public ProviderAnnotationHandler providerAnnotationHandler() {
        return new ProviderAnnotationHandler();
    }

    @Bean
    public RegistryCenter registryCenter() {
        return new ZKRegistryCenter();
    }
}
