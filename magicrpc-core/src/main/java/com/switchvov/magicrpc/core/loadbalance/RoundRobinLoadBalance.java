package com.switchvov.magicrpc.core.loadbalance;

import java.util.List;

/**
 * @author switch
 * @since 2024/3/11
 */
public class RoundRobinLoadBalance implements LoadBalance {
    private List<String> hosts;
    private int index;

    public RoundRobinLoadBalance(List<String> hosts) {
        this.hosts = hosts;
        this.index = 0;
    }

    @Override
    public String select() {
        if (index++ < 0) {
            index = 0;
        }
        return hosts.get(index % hosts.size());
    }
}
