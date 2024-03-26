package com.switchvov.magicrpc.core.filter;

import com.switchvov.magicrpc.core.api.Filter;
import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;
import com.switchvov.magicrpc.core.util.MethodUtils;
import com.switchvov.magicrpc.core.util.MockUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author switch
 * @since 2024/3/26
 */
public class MockFilter implements Filter {
    @SneakyThrows
    @Override
    public Object preFilter(RpcRequest request) {
        Class<?> service = Class.forName(request.getService());
        Method method = findMethod(service, request.getMethodSign());
        Class<?> clazz = method.getReturnType();
        return MockUtils.mock(clazz);
    }

    private Method findMethod(Class<?> service, String methodSign) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtils.checkLocalMethod(method))
                .filter(method -> methodSign.equals(MethodUtils.methodSign(method)))
                .findFirst().orElse(null);
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        return null;
    }
}
