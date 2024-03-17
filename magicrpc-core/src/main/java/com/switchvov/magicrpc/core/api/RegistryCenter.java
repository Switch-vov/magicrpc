package com.switchvov.magicrpc.core.api;

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
     * @param service
     * @param instance
     */
    void register(String service, RegistryNode instance);

    /**
     * for provider
     *
     * @param service
     * @param instance
     */
    void unregister(String service, RegistryNode instance);

    /**
     * for consumer
     *
     * @param service
     * @return
     */
    List<RegistryNode> fetchAll(String service);

    // void subscribe();

    class StaticRegistryCenter implements RegistryCenter {
        private final List<RegistryNode> providers;

        public StaticRegistryCenter(List<RegistryNode> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, RegistryNode instance) {

        }

        @Override
        public void unregister(String service, RegistryNode instance) {

        }

        @Override
        public List<RegistryNode> fetchAll(String service) {
            return providers;
        }
    }
}
