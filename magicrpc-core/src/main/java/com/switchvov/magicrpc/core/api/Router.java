package com.switchvov.magicrpc.core.api;

import java.util.List;

/**
 * @author switch
 * @since 2024/3/16
 */
public interface Router<T> {
    List<T> route(List<T> providers);

    Router<?> DEFAULT = p -> p;
}
