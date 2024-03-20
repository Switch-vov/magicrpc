package com.switchvov.magicrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author switch
 * @since 2024/3/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest {
    /**
     * 接口
     */
    private String service;
    /**
     * 方法
     */
    private String methodSign;
    /**
     * 参数
     */
    private Object[] args;
}
