package com.switchvov.magicrpc.core.provider;

import com.switchvov.magicrpc.core.annotation.MagicProvider;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author switch
 * @since 2024/3/7
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProviderBootstrap.class);

    private ApplicationContext applicationContext;

    private final Map<String, Object> SKELETON = new HashMap<>();

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
                Object result = method.invoke(bean, request.getArgs());
                response.setStatus(true);
                response.setData(result);
            }
        } catch (IllegalAccessException e) {
            response.setEx(e.getMessage());
        } catch (InvocationTargetException e) {
            response.setEx(e.getTargetException().getMessage());
        }
        return response;
    }

    private Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
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
