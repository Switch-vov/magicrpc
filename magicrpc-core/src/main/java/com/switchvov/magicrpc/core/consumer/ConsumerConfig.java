package com.switchvov.magicrpc.core.consumer;

import com.switchvov.magicrpc.core.api.LoadBalancer;
import com.switchvov.magicrpc.core.api.RegistryCenter;
import com.switchvov.magicrpc.core.api.RegistryNode;
import com.switchvov.magicrpc.core.api.Router;
import com.switchvov.magicrpc.core.cluster.WeightedRoundRobinLoadBalancer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;

/**
 * @author switch
 * @since 2024/3/10
 */
@Configuration
public class ConsumerConfig {
    @Value("${magicrpc.providers}")
    private String servers;

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrapRunner(ConsumerBootstrap consumerBootstrap) {
        return x -> {
            consumerBootstrap.start();
        };
    }

    @Bean
    public LoadBalancer loadBalancer() {
        return new WeightedRoundRobinLoadBalancer();
    }

    @Bean
    public Router router() {
        return Router.DEFAULT;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter() {
        List<RegistryNode> providers = Arrays.stream(servers.split(","))
                .map(server -> new RegistryNode(server, 0))
                .toList();
        for (int i = 0; i < providers.size(); i++) {
            if (i == 0) {
                providers.get(i).setWeight(7);
            }
            if (i == 1) {
                providers.get(i).setWeight(2);
            }
            if (i == 2) {
                providers.get(i).setWeight(1);
            }
        }
        return new RegistryCenter.StaticRegistryCenter(providers);
    }
}
