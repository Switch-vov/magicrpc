package com.switchvov.magicrpc.core.consumer;

import com.switchvov.magicrpc.core.api.Filter;
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
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 消费端动态代理
 *
 * @author switch
 * @since 2024/3/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MagicInvocationHandler implements InvocationHandler {

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

        for (Filter filter : Optional.ofNullable(this.context.getFilters()).orElse(new ArrayList<>())) {
            RpcResponse preResponse = filter.preFilter(request);
            if (!Objects.isNull(preResponse)) {
                log.debug("{} ==> prefilter: {}", filter.getClass().getName(), preResponse);
                return castReturnResult(method, preResponse);
            }
        }

        List<InstanceMeta> instanceMetas = context.getRouter().route(providers);
        InstanceMeta instanceMeta = context.getLoadBalancer().choose(instanceMetas);
        log.debug("loadBalancer.choose(instanceMetas) ==> {}", instanceMeta.toString());
        RpcResponse<?> response = invoker.post(request, instanceMeta.toUrl());

        for (Filter filter : Optional.ofNullable(this.context.getFilters()).orElse(new ArrayList<>())) {
            response = filter.postFilter(request, response);
        }

        return castReturnResult(method, response);
    }

    private static Object castReturnResult(Method method, RpcResponse<?> response) {
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
