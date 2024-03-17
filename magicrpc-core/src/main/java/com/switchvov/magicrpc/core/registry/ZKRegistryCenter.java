package com.switchvov.magicrpc.core.registry;

import com.switchvov.magicrpc.core.api.RegistryCenter;
import com.switchvov.magicrpc.core.api.RegistryNode;

import java.util.List;

/**
 * @author switch
 * @since 2024/3/16
 */
public class ZKRegistryCenter implements RegistryCenter {
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
        return null;
    }
}
