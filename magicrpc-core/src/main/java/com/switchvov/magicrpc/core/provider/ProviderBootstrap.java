package com.switchvov.magicrpc.core.provider;

import com.switchvov.magicrpc.core.annotation.MagicProvider;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.meta.ProviderMeta;
import com.switchvov.magicrpc.core.util.MethodUtils;
import com.switchvov.magicrpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author switch
 * @since 2024/3/7
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProviderBootstrap.class);

    private ApplicationContext applicationContext;

    private final MultiValueMap<String, ProviderMeta> SKELETON = new LinkedMultiValueMap<>();

    @PostConstruct
    public void start() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(MagicProvider.class);
        providers.forEach((x, y) -> LOGGER.debug("server impl:" + x));
        providers.values().forEach(this::getInterface);
    }

    private void getInterface(final Object obj) {
        Arrays.stream(obj.getClass().getInterfaces()).forEach(inter -> {
            Method[] methods = inter.getMethods();
            for (Method method : methods) {
                if (MethodUtils.checkLocalMethod(method)) {
                    continue;
                }
                createProvider(inter, method, obj);
            }
        });
    }

    private void createProvider(Class<?> inter, Method method, Object obj) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setServiceImpl(obj);
        meta.setMethodSign(MethodUtils.methodSign(method));
        LOGGER.info("create a provider:{}", meta);
        SKELETON.add(inter.getCanonicalName(), meta);
    }

    public RpcResponse<?> invoke(RpcRequest request) {
        RpcResponse<Object> response = new RpcResponse<>();
        List<ProviderMeta> providerMetas = SKELETON.get(request.getService());
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            if (Objects.isNull(meta)) {
                return null;
            }
            Method method = meta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            response.setStatus(true);
            response.setData(result);
        } catch (IllegalAccessException e) {
            response.setEx(e.getMessage());
        } catch (InvocationTargetException e) {
            response.setEx(e.getTargetException().getMessage());
        }
        return response;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (Objects.isNull(args) || args.length == 0) {
            return args;
        }
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actuals[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return actuals;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        return providerMetas.stream()
                .filter(x -> x.getMethodSign().equals(methodSign))
                .findFirst()
                .orElse(null);
    }
}
