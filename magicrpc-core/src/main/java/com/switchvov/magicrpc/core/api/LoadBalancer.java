package com.switchvov.magicrpc.core.api;

import java.util.List;
import java.util.Objects;

/**
 * @author switch
 * @since 2024/3/16
 */
public interface LoadBalancer<T> {
    T choose(List<T> providers);

    LoadBalancer<?> DEFAULT = p -> (Objects.isNull(p) || p.isEmpty()) ? null : p.get(0);
}
