package com.switchvov.magicrpc.core.api;

import com.switchvov.magicrpc.core.meta.InstanceMeta;
import com.switchvov.magicrpc.core.meta.ServiceMeta;
import com.switchvov.magicrpc.core.registry.ChangeListener;

import java.util.List;

/**
 * @author switch
 * @since 2024/3/16
 */
public interface RegistryCenter {
    /**
     * for register service
     */
    void start();

    /**
     * for register service
     */
    void stop();

    /**
     * for provider
     *
     * @param service  服务定义
     * @param instance 实例定义
     */
    void register(ServiceMeta service, InstanceMeta instance);

    /**
     * for provider
     *
     * @param service  服务定义
     * @param instance 实例定义
     */
    void unregister(ServiceMeta service, InstanceMeta instance);

    /**
     * for consumer
     *
     * @param service 服务定义
     * @return 实例定义列表
     */
    List<InstanceMeta> fetchAll(ServiceMeta service);

    /**
     * 订阅
     *
     * @param service  服务定义
     * @param listener 监听器
     */
    void subscribe(ServiceMeta service, ChangeListener listener);

    class StaticRegistryCenter implements RegistryCenter {
        private final List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangeListener listener) {

        }
    }
}
