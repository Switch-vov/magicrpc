package com.switchvov.magicrpc.core.consumer;

import com.switchvov.magicrpc.core.annotation.MagicConsumer;
import com.switchvov.magicrpc.core.api.*;
import com.switchvov.magicrpc.core.meta.InstanceMeta;
import com.switchvov.magicrpc.core.meta.ServiceMeta;
import com.switchvov.magicrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消费端启动类
 *
 * @author switch
 * @since 2024/3/10
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware {

    private final Map<String, Object> STUB = new HashMap<>();

    private ApplicationContext applicationContext;

    @Value("${server.port}")
    private String port;
    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;


    public void start() {
        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().toList();

        RpcContext context = RpcContext.builder()
                .router(router)
                .loadBalancer(loadBalancer)
                .filters(filters)
                .build();

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass(), MagicConsumer.class);
            fields.forEach(field -> {
                try {
                    Class<?> service = field.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = STUB.computeIfAbsent(serviceName, s -> createFromRegistry(service, context, registryCenter));
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    private Object createFromRegistry(Class<?> service, RpcContext context, RegistryCenter registryCenter) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service.getCanonicalName())
                .app(app)
                .namespace(namespace)
                .env(env)
                .build();
        List<InstanceMeta> providers = new ArrayList<>(registryCenter.fetchAll(serviceMeta));
        log.debug(" ===> get service:{} from registry, provider: {}", serviceMeta.toPath(), providers);

        registryCenter.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
            log.debug(" ===> get service:{} from registry, provider: {}", serviceMeta.toPath(), providers);
        });

        return createConsumer(service, context, providers);
    }


    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class[]{service},
                new MagicInvocationHandler(service, context, providers)
        );
    }
}
