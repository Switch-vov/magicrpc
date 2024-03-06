package com.switchvov.magicrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author switch
 * @since 2024/3/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> {
    /**
     * 状态
     */
    private boolean status;
    /**
     * 数据
     */
    private T data;
}
