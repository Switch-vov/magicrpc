package com.switchvov.magicrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述服务元数据
 *
 * @author switch
 * @since 2024/3/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceMeta {
    private String app;
    private String namespace;
    private String env;
    private String name;

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }
}
