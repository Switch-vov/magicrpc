package com.switchvov.magicrpc.core.api;

/**
 * 过滤器
 *
 * @author switch
 * @since 2024/3/16
 */
public interface Filter {
    Object preFilter(RpcRequest request);

    Object postFilter(RpcRequest request, RpcResponse response, Object result);

    Filter DEFAULT = new Filter() {
        @Override
        public RpcResponse preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }
    };
}
