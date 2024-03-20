package com.switchvov.magicrpc.core.consumer;

import com.switchvov.magicrpc.core.api.RpcRequest;
import com.switchvov.magicrpc.core.api.RpcResponse;

/**
 * @author switch
 * @since 2024/3/20
 */
public interface HttpInvoker {
    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
