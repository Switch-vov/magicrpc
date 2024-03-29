package com.switchvov.magicrpc.core.provider;

import com.switchvov.magicrpc.core.annotation.MagicProvider;
import com.switchvov.magicrpc.core.api.RegistryCenter;
import com.switchvov.magicrpc.core.meta.InstanceMeta;
import com.switchvov.magicrpc.core.meta.ProviderMeta;
import com.switchvov.magicrpc.core.meta.ServiceMeta;
import com.switchvov.magicrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * 服务提供者启动类
 *
 * @author switch
 * @since 2024/3/7
 */
@Data
@Slf4j
public class ProviderBootstrap implements ApplicationContextAware {
    private final MultiValueMap<String, ProviderMeta> SKELETON = new LinkedMultiValueMap<>();

    private ApplicationContext applicationContext;
    private RegistryCenter rc;
    private InstanceMeta instance;

    @Value("${server.port}")
    private String port;
    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;

    @PostConstruct
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(MagicProvider.class);
        providers.forEach((x, y) -> log.debug("server impl:" + x));
        providers.values().forEach(this::getInterface);
        rc = applicationContext.getBean(RegistryCenter.class);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = InstanceMeta.http(ip, Integer.valueOf(port));
        rc.start();
        SKELETON.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        SKELETON.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(app)
                .namespace(namespace)
                .env(env)
                .build();
        rc.register(serviceMeta, instance);
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(app)
                .namespace(namespace)
                .env(env)
                .build();
        rc.unregister(serviceMeta, instance);
    }

    private void getInterface(final Object obj) {
        Arrays.stream(obj.getClass().getInterfaces()).forEach(
                service -> Arrays.stream(service.getMethods())
                        .filter(method -> !MethodUtils.checkLocalMethod(method))
                        .forEach(method -> createProvider(service, method, obj))
        );
    }

    private void createProvider(Class<?> inter, Method method, Object obj) {
        ProviderMeta meta = ProviderMeta.builder()
                .method(method)
                .serviceImpl(obj)
                .methodSign(MethodUtils.methodSign(method))
                .build();
        log.info("create a provider:{}", meta);
        SKELETON.add(inter.getCanonicalName(), meta);
    }
}
