package com.switchvov.magicrpc.core.api;

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
     * @param service
     * @param instance
     */
    void register(String service, String instance);

    /**
     * for provider
     *
     * @param service
     * @param instance
     */
    void unregister(String service, String instance);

    /**
     * for consumer
     *
     * @param service
     * @return
     */
    List<String> fetchAll(String service);

     void subscribe(String service, ChangeListener listener);

    class StaticRegistryCenter implements RegistryCenter {
        private final List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangeListener listener) {

        }
    }
}
