package com.switchvov.magicrpc.core.cluster;

import com.switchvov.magicrpc.core.api.LoadBalancer;
import com.switchvov.magicrpc.core.api.RegistryNode;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author switch
 * @since 2024/3/17
 */
public class WeightedRoundRobinLoadBalancer implements LoadBalancer<RegistryNode> {

    private ConcurrentMap<String, WeightedRoundRobin> map = new ConcurrentHashMap<>();

    @Getter
    public static class WeightedRoundRobin {
        private int weight;
        private AtomicLong currentWeight;

        public WeightedRoundRobin(int weight) {
            this.weight = weight;
            currentWeight = new AtomicLong(0);
        }

        public void setWeight(int weight) {
            this.weight = weight;
            currentWeight = new AtomicLong(0);
        }

        public long incrCurrent() {
            return currentWeight.addAndGet(weight);
        }

        public void sel(long total) {
            currentWeight.addAndGet(-1 * total);
        }
    }

    @Override
    public RegistryNode choose(List<RegistryNode> providers) {
        if (Objects.isNull(providers) || providers.isEmpty()) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        Map.Entry<String, RegistryNode>[] entries = providers.stream()
                .map(registryNode -> Map.entry(registryNode.getServerUrl(), registryNode))
                .toArray(Map.Entry[]::new);
        Map<String, RegistryNode> nodeMap = Map.ofEntries(entries);
        for (String s : map.keySet()) {
            if (Objects.isNull(nodeMap.get(s))) {
                map.remove(s);
            }
        }


        long totalWeight = 0;
        long maxCur = Long.MIN_VALUE;
        RegistryNode selectNode = null;
        WeightedRoundRobin selectRobin = null;
        for (RegistryNode provider : providers) {
            WeightedRoundRobin roundRobin = map.computeIfAbsent(provider.getServerUrl(),
                    server -> new WeightedRoundRobin(provider.getWeight()));
            int weight = provider.getWeight();
            if (weight != roundRobin.getWeight()) {
                // weight changed
                roundRobin.setWeight(weight);
            }

            long cur = roundRobin.incrCurrent();
            if (cur > maxCur) {
                maxCur = cur;
                selectNode = provider;
                selectRobin = roundRobin;
            }
            totalWeight += weight;
        }
        if (!Objects.isNull(selectRobin)) {
            selectRobin.sel(totalWeight);
            return selectNode;
        }
        return providers.get(0);
    }
}
