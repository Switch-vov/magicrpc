package com.switchvov.magicrpc.core.api;

import lombok.Data;

import java.util.List;

/**
 * @author switch
 * @since 2024/3/16
 */
@Data
public class RpcContext {
    // TODO
    private List<Filter> filters;
    private Router router;
    private LoadBalancer loadBalancer;
}
