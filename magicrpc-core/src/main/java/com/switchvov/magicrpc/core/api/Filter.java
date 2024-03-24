package com.switchvov.magicrpc.core.api;

/**
 * 过滤器
 *
 * @author switch
 * @since 2024/3/16
 */
public interface Filter {
    RpcResponse preFilter(RpcRequest request);

    RpcResponse postFilter(RpcRequest request, RpcResponse response);

    Filter DEFAULT = new Filter() {
        @Override
        public RpcResponse preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public RpcResponse postFilter(RpcRequest request, RpcResponse response) {
            return response;
        }
    };
}
