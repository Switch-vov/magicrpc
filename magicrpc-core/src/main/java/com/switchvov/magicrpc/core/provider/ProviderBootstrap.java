package com.switchvov.magicrpc.core.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switchvov.magicrpc.core.annotation.MagicProvider;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.util.MethodUtils;
import com.switchvov.magicrpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @author switch
 * @since 2024/3/7
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProviderBootstrap.class);

    private ApplicationContext applicationContext;

    private final Map<String, Object> SKELETON = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RpcResponse<?> invokeRequest(RpcRequest request) {
        String methodName = request.getMethod();
        if (MethodUtils.checkLocalMethod(methodName)) {
            return null;
        }

        RpcResponse<Object> response = new RpcResponse<>();
        Object bean = SKELETON.get(request.getService());
        try {
            Method method = findMethod(bean.getClass(), request.getMethod());
            if (method != null) {
                Class<?>[] paramTypes = method.getParameterTypes();
                Object[] args = request.getArgs();
                if (!Objects.isNull(args)) {
                    args = IntStream.range(0, request.getArgs().length).mapToObj(i -> {
                        Class<?> paramType = paramTypes[i];
                        try {
                            String arg = objectMapper.writeValueAsString(request.getArgs()[i]);
                            return objectMapper.readValue(arg, paramType);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }).toArray();
                }
                Object result = method.invoke(bean, args);
                response.setStatus(true);
                response.setData(result);
            }
        } catch (InvocationTargetException e) {
            response.setEx(e.getTargetException().getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            response.setEx(e.getMessage());
        }
        return response;
    }

    private Method findMethod(Class<?> aClass, String methodSign) {
        String methodName = MethodUtils.getMethodNameBySign(methodSign);
        String[] params = MethodUtils.getMethodParamTypesBySign(methodSign);
        Class<?>[] classes = Arrays.stream(params)
                .map(className -> {
                    try {
                        return Class.forName(className);
                    } catch (ClassNotFoundException e) {
                        try {
                            return TypeUtils.primitiveTypeForName(className);
                        } catch (NoSuchFieldException | IllegalAccessException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                })
                .toArray(Class[]::new);
        try {
            return aClass.getMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(MagicProvider.class);
        providers.forEach((x, y) -> LOGGER.debug("server impl:" + x));
        providers.values().forEach(x -> {
            Class<?> inter = x.getClass().getInterfaces()[0];
            SKELETON.put(inter.getCanonicalName(), x);
        });
    }

}
