package com.switchvov.magicrpc.core.consumer;

import com.switchvov.magicrpc.core.api.RpcContext;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.consumer.http.OkHttpInvoker;
import com.switchvov.magicrpc.core.meta.InstanceMeta;
import com.switchvov.magicrpc.core.util.MethodUtils;
import com.switchvov.magicrpc.core.util.TypeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * 消费端动态代理
 *
 * @author switch
 * @since 2024/3/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MagicInvocationHandler implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MagicInvocationHandler.class);

    private final HttpInvoker invoker = new OkHttpInvoker();

    private Class<?> service;
    private RpcContext context;
    private List<InstanceMeta> providers;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest request = RpcRequest.builder()
                .service(service.getCanonicalName())
                .methodSign(MethodUtils.methodSign(method))
                .args(args)
                .build();

        List<InstanceMeta> instanceMetas = context.getRouter().route(providers);
        InstanceMeta instanceMeta = context.getLoadBalancer().choose(instanceMetas);
        LOGGER.debug("loadBalancer.choose(instanceMetas) ==> {}", instanceMeta.toString());
        RpcResponse<?> response = invoker.post(request, instanceMeta.toUrl());

        if (Objects.isNull(response)) {
            return null;
        }
        if (response.isStatus()) {
            Object data = response.getData();
            return TypeUtils.castMethodResult(method, data);
        }
        throw new RuntimeException(response.getEx());
    }

}
