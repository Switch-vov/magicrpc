package com.switchvov.magicrpc.core.registry.zookeeper;

import com.switchvov.magicrpc.core.api.RegistryCenter;
import com.switchvov.magicrpc.core.meta.InstanceMeta;
import com.switchvov.magicrpc.core.meta.ServiceMeta;
import com.switchvov.magicrpc.core.registry.ChangeListener;
import com.switchvov.magicrpc.core.registry.Event;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author switch
 * @since 2024/3/16
 */
@Slf4j
public class ZKRegistryCenter implements RegistryCenter {
    private final ConcurrentMap<ServiceMeta, TreeCache> CACHE_MAP = new ConcurrentHashMap<>();
    private CuratorFramework client = null;

    @Value("${magicrpc.zk_server}")
    private String zkServer;
    @Value("${magicrpc.zk_root}")
    private String zkRoot;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkServer)
                .namespace(zkRoot)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        log.debug(" ===> zk client started to server[{}/{}].", zkServer, zkRoot);
    }

    @Override
    public void stop() {
        log.debug(" ===> zk client stopped.");
        for (TreeCache cache : CACHE_MAP.values()) {
            if (Objects.isNull(cache)) {
                continue;
            }
            cache.close();
        }
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                // 创建服务的持久化节点
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance.toPath();
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            log.debug(" ===> register to zk: {}", instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 判断服务是否存在
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                return;
            }
            // 删除实例节点
            String instancePath = servicePath + "/" + instance.toPath();
            client.delete().quietly().forPath(instancePath);
            log.debug(" ===> unregister from zk: {}", instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service.toPath();
        try {
            // 获取所有子节点
            List<String> nodes = client.getChildren().forPath(servicePath);
            log.debug(" ===> fetchAll from zk:{}", servicePath);
            return nodes.stream().map(x -> {
                String[] strs = x.split("_");
                return InstanceMeta.http(strs[0], Integer.valueOf(strs[1]));
            }).toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        TreeCache cache = CACHE_MAP.computeIfAbsent(service,
                serviceMeta -> TreeCache.newBuilder(client, "/" + service)
                        .setCacheData(true)
                        .setMaxDepth(2)
                        .build()
        );

        cache.getListenable().addListener((curator, event) -> {
            // 有任何节点变动这里会执行
            log.debug(" ===> zk subscribe event: " + event);
            List<InstanceMeta> nodes = fetchAll(service);
            listener.fire(new Event(nodes));
        });
        cache.start();
    }
}
