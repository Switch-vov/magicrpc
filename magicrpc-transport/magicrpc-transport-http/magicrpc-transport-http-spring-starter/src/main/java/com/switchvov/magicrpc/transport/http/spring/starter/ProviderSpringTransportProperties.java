package com.switchvov.magicrpc.transport.http.spring.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author switch
 * @since 2024/3/23
 */
@ConfigurationProperties(prefix = "magicrpc.transport.http.spring")
@Data
public class ProviderSpringTransportProperties {
    private boolean enable = true;
    private String context = "/";
}
