package com.switchvov.magicrpc.core.provider;

import com.switchvov.magicrpc.core.api.RpcException;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.meta.ProviderMeta;
import com.switchvov.magicrpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author switch
 * @since 2024/3/20
 */
public class ProviderInvoker {
    private final MultiValueMap<String, ProviderMeta> skeleton;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeleton = providerBootstrap.getSKELETON();
    }

    public RpcResponse<?> invoke(RpcRequest request) {
        RpcResponse<Object> response = new RpcResponse<>();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
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
            response.setEx(new RpcException(e.getMessage()));
        } catch (InvocationTargetException e) {
            response.setEx(new RpcException(e.getTargetException().getMessage()));
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
