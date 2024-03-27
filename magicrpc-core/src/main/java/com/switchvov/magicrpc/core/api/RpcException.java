package com.switchvov.magicrpc.core.api;

import lombok.EqualsAndHashCode;

/**
 * @author switch
 * @since 2024/3/27
 */
@EqualsAndHashCode(callSuper = true)
public class RpcException extends RuntimeException {
    private String errcode;

    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(Throwable cause, String errcode) {
        super(cause);
        this.errcode = errcode;
    }

    // X => 技术类异常：
    // Y => 业务类异常：
    // Z => unknown，尚未能分类异常
    public static final String SOCKET_TIMEOUT_EX = "X001" + "-" + "http_invoke_timeout";
    public static final String NO_SUCH_METHOD_EX = "X002" + "-" + "method_not_exists";
    public static final String UNKNOWN_EX = "Z001" + "-" + "unknown";
}
