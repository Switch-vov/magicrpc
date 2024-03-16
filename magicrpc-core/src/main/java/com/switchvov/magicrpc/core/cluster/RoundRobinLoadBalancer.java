package com.switchvov.magicrpc.core.cluster;

import com.switchvov.magicrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author switch
 * @since 2024/3/16
 */
public class RoundRobinLoadBalancer<T> implements LoadBalancer<T> {

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public T choose(List<T> providers) {
        if (Objects.isNull(providers) || providers.isEmpty()) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return providers.get((index.getAndIncrement() & Integer.MAX_VALUE) % providers.size());
    }
}
