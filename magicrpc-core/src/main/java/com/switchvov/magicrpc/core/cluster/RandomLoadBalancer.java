package com.switchvov.magicrpc.core.cluster;

import com.switchvov.magicrpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author switch
 * @since 2024/3/16
 */
public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @Override
    public T choose(List<T> providers) {
        if (Objects.isNull(providers) || providers.isEmpty()) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return providers.get(RANDOM.nextInt(providers.size()));
    }
}
