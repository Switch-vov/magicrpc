package com.switchvov.magicrpc.core.api;

import com.switchvov.magicrpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author switch
 * @since 2024/3/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcContext {
    /**
     * TODO
     */
    private List<Filter> filters;
    private Router<InstanceMeta> router;
    private LoadBalancer<InstanceMeta> loadBalancer;
}
