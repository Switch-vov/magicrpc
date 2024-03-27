package com.switchvov.magicrpc.core.consumer;

import com.switchvov.magicrpc.core.api.Filter;
import com.switchvov.magicrpc.core.api.LoadBalancer;
import com.switchvov.magicrpc.core.api.RegistryCenter;
import com.switchvov.magicrpc.core.api.Router;
import com.switchvov.magicrpc.core.cluster.RoundRobinLoadBalancer;
import com.switchvov.magicrpc.core.meta.InstanceMeta;
import com.switchvov.magicrpc.core.registry.zookeeper.ZKRegistryCenter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


/**
 * @author switch
 * @since 2024/3/10
 */
@Configuration
public class ConsumerConfig {
    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrapRunner(ConsumerBootstrap consumerBootstrap) {
        return x -> consumerBootstrap.start();
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
        return new RoundRobinLoadBalancer<>();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return Router.DEFAULT;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter() {
        return new ZKRegistryCenter();
    }

    @Bean
    public Filter defaultFilter() {
        return Filter.DEFAULT;
    }

//    @Bean
//    public Filter cacheFilter() {
//        return new CacheFilter();
//    }

//    @Bean
//    public Filter mockFilter() {
//        return new MockFilter();
//    }
}
