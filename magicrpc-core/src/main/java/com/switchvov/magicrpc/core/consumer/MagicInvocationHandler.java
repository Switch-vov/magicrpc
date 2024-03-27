package com.switchvov.magicrpc.core.consumer;

import com.switchvov.magicrpc.core.api.Filter;
import com.switchvov.magicrpc.core.api.RpcContext;
import com.switchvov.magicrpc.core.api.RpcException;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.consumer.http.OkHttpInvoker;
import com.switchvov.magicrpc.core.meta.InstanceMeta;
import com.switchvov.magicrpc.core.util.MethodUtils;
import com.switchvov.magicrpc.core.util.TypeUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
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
@Slf4j
public class MagicInvocationHandler implements InvocationHandler {

    private final Class<?> service;
    private final RpcContext context;
    private final List<InstanceMeta> providers;
    private final HttpInvoker invoker;

    public MagicInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("app.timeout", "1000"));
        this.invoker = new OkHttpInvoker(timeout);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RpcRequest request = RpcRequest.builder()
                .service(service.getCanonicalName())
                .methodSign(MethodUtils.methodSign(method))
                .args(args)
                .build();

        int retries = Integer.parseInt(context.getParameters().getOrDefault("app.retries", "1"));
        while (retries-- > 0) {
            log.debug(" ===> reties: {}", retries);
            try {
                for (Filter filter : Optional.ofNullable(this.context.getFilters()).orElse(new ArrayList<>())) {
                    Object preResult = filter.preFilter(request);
                    if (!Objects.isNull(preResult)) {
                        log.debug("{} ==> prefilter: {}", filter.getClass().getName(), preResult);
                        return preResult;
                    }
                }

                List<InstanceMeta> instances = context.getRouter().route(providers);
                InstanceMeta instanceMeta = context.getLoadBalancer().choose(instances);
                log.debug("loadBalancer.choose(instanceMetas) ==> {}", instanceMeta.toString());

                RpcResponse<?> response = invoker.post(request, instanceMeta.toUrl());
                Object result = castReturnResult(method, response);

                for (Filter filter : Optional.ofNullable(this.context.getFilters()).orElse(new ArrayList<>())) {
                    Object filterResult = filter.postFilter(request, response, result);
                    if (!Objects.isNull(filterResult)) {
                        return filterResult;
                    }
                }
                return result;
            } catch (Exception ex) {
                if (!(ex.getCause() instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }
        return null;
    }

    private static Object castReturnResult(Method method, RpcResponse<?> response) {
        if (Objects.isNull(response)) {
            return null;
        }
        if (response.isStatus()) {
            return TypeUtils.castMethodResult(method, response.getData());
        }
        Exception exception = response.getEx();
        if (exception instanceof RpcException ex) {
            throw ex;
        }
        throw new RpcException(exception, RpcException.UNKNOWN_EX);
    }

}
