package com.switchvov.magicrpc.core.api;

import com.switchvov.magicrpc.core.meta.InstanceMeta;

import java.util.List;

/**
 * 路由器
 *
 * @author switch
 * @since 2024/3/16
 */
public interface Router<T> {
    /**
     * @param providers
     * @return
     */
    List<T> route(List<T> providers);

    Router<InstanceMeta> DEFAULT = p -> p;
}
