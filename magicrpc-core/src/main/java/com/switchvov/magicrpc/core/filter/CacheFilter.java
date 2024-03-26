package com.switchvov.magicrpc.core.filter;

import com.switchvov.magicrpc.core.api.Filter;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author switch
 * @since 2024/3/26
 */
public class CacheFilter implements Filter {

    public static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    @Override
    public Object preFilter(RpcRequest request) {
        return CACHE.get(request.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        if (Objects.isNull(result)) {
            return null;
        }
        return CACHE.putIfAbsent(request.toString(), result);
    }
}
