package com.switchvov.magicrpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 描述服务实例的元数据
 *
 * @author switch
 * @since 2024/3/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {
    private String schema;
    private String host;
    private Integer port;
    private String context;
    /**
     * true:online;false:offline
     */
    private boolean status;
    private Map<String, String> parameters;

    public InstanceMeta(String schema, String host, Integer port, String context) {
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public String toUrl() {
        return String.format("%s://%s:%d/%s", schema, host, port, context);
    }

    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "");
    }
}
