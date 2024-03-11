package com.switchvov.magicrpc.core.loadbalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author switch
 * @since 2024/3/11
 */
public class RandomLoadBalance implements LoadBalance {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private List<String> hosts;

    public RandomLoadBalance(List<String> hosts) {
        this.hosts = hosts;
    }

    @Override
    public String select() {
        return hosts.get(RANDOM.nextInt(hosts.size()));
    }
}
