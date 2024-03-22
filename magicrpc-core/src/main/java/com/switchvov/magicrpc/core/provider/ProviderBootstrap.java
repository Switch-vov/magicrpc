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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

/**
 * 服务提供者启动类
 *
 * @author switch
 * @since 2024/3/7
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProviderBootstrap.class);

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
        ProviderAnnotationHandler handler = applicationContext.getBean(ProviderAnnotationHandler.class);
        List<Class<?>> providers = handler.handle(new Class[]{MagicProvider.class});
        providers.forEach(provider -> LOGGER.debug("server class:" + provider));
        providers.forEach(this::getInterface);
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

    @SneakyThrows
    private void getInterface(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        int maxLength = 0;
        for (Constructor<?> constructor : constructors) {
            if (maxLength < constructor.getParameterCount()) {
                maxLength = constructor.getParameterCount();
            }

        }
        Object obj = null;
        for (Constructor<?> constructor : constructors) {
            if (maxLength == constructor.getParameterCount()) {
                Object[] args = Arrays.stream(constructor.getParameterTypes())
                        .map(paramClass -> applicationContext.getBean(paramClass))
                        .toArray();
                obj = constructor.newInstance(args);
            }
        }
        final Object finalObj = obj;
        Arrays.stream(clazz.getInterfaces()).forEach(inter -> {
            Method[] methods = inter.getMethods();
            for (Method method : methods) {
                if (MethodUtils.checkLocalMethod(method)) {
                    continue;
                }
                createProvider(inter, method, finalObj);
            }
        });
    }

    private void createProvider(Class<?> inter, Method method, Object obj) {
        ProviderMeta meta = ProviderMeta.builder()
                .method(method)
                .serviceImpl(obj)
                .methodSign(MethodUtils.methodSign(method))
                .build();
        LOGGER.debug("create a provider:{}", meta);
        SKELETON.add(inter.getCanonicalName(), meta);
    }
}
