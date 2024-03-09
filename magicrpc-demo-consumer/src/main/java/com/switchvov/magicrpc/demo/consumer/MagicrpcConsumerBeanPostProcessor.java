package com.switchvov.magicrpc.demo.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchvov.magicrpc.core.annotation.MagicConsumer;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.register.client.annotation.RegisterCli;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Random;

/**
 * @author switch
 * @since 2024/3/8
 */
@Component
@Data
public class MagicrpcConsumerBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(MagicrpcConsumerBeanPostProcessor.class);

    private final RestClient restClient = RestClient.builder().build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(MagicConsumer.class) != null) {
                Class<?> clazz = field.getType();
                LOGGER.info("field name:{},field class:{}", field.getName(), clazz);
                Class<?>[] interfaces;
                if (clazz.isInterface()) {
                    interfaces = new Class[]{clazz};
                } else {
                    interfaces = clazz.getInterfaces();
                }
                Object value = Proxy.newProxyInstance(
                        clazz.getClassLoader(),
                        interfaces,
                        (proxy, method, args) -> {
                            String service = method.getDeclaringClass().getCanonicalName();
                            RegisterCli registerCli = applicationContext.getBean(RegisterCli.class);
                            List<String> hosts = registerCli.getHosts(service);
                            Random r = new Random();
                            String host = hosts.get(r.nextInt(hosts.size()));
                            LOGGER.info("get hosts:{}, random host:{}", hosts, host);
                            RpcRequest request = new RpcRequest();
                            request.setService(service);
                            request.setMethod(method.getName());
                            request.setArgs(args);
                            ResponseEntity<RpcResponse> response = restClient.post()
                                    .uri("http://" + host)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(request)
                                    .retrieve()
                                    .toEntity(RpcResponse.class);
                            String jsonStr = objectMapper.writeValueAsString(response.getBody().getData());
                            return objectMapper.readValue(jsonStr, method.getReturnType());
                        }
                );
                try {
                    field.setAccessible(true);
                    field.set(bean, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return bean;
    }
}
