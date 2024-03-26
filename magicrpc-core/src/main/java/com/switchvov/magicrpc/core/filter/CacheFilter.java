package com.switchvov.magicrpc.core.filter;

import com.switchvov.magicrpc.core.api.Filter;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CacheFilter implements Filter {

    public static final Map<String, RpcResponse<?>> CACHE = new ConcurrentHashMap<>();

    @Override
    public RpcResponse preFilter(RpcRequest request) {
        return CACHE.get(request.toString());
    }

    @Override
    public RpcResponse postFilter(RpcRequest request, RpcResponse response) {
        if (Objects.isNull(response)) {
            return null;
        }
        CACHE.putIfAbsent(request.toString(), response);
        return response;
    }
}
