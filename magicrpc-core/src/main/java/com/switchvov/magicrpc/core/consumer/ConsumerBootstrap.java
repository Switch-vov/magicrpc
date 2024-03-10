package com.switchvov.magicrpc.core.consumer;

import com.switchvov.magicrpc.core.annotation.MagicConsumer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author switch
 * @since 2024/3/10
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(ConsumerBootstrap.class);

    private ApplicationContext applicationContext;
    private final Map<String, Object> STUB = new HashMap<>();

    public void start() {
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotatedField(bean.getClass());
            fields.forEach(field -> {
                try {
                    Class<?> service = field.getType();
                    String serviceName = service.getCanonicalName();
                    if (!STUB.containsKey(serviceName)) {
                        STUB.put(serviceName, createConsumer(service));
                    }
                    field.setAccessible(true);
                    field.set(bean, STUB.get(serviceName));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

    }

    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(
                service.getClassLoader(),
                new Class[]{service},
                new MagicInvocationHandler(service)
        );
    }

    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> fields = new ArrayList<>();
        while (aClass != null) {
            fields.addAll(Arrays.stream(aClass.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(MagicConsumer.class))
                    .toList());
            aClass = aClass.getSuperclass();
        }
        return fields;
    }
}
