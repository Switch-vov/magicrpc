package com.switchvov.magicrpc.core.api;

import java.util.List;
import java.util.Objects;

/**
 * 负载均衡
 *
 * @author switch
 * @since 2024/3/16
 */
public interface LoadBalancer<T> {
    /**
     * @param providers
     * @return
     */
    T choose(List<T> providers);

    LoadBalancer<?> DEFAULT = p -> (Objects.isNull(p) || p.isEmpty()) ? null : p.get(0);
}
